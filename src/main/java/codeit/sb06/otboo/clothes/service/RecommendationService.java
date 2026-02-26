package codeit.sb06.otboo.clothes.service;

import codeit.sb06.otboo.clothes.dto.ClothesDto;
import codeit.sb06.otboo.clothes.dto.RecommendationDto;
import codeit.sb06.otboo.clothes.dto.RecommendedClothesDto;
import codeit.sb06.otboo.clothes.entity.Clothes;
import codeit.sb06.otboo.clothes.entity.ClothesAttribute;
import codeit.sb06.otboo.clothes.entity.ClothesType;
import codeit.sb06.otboo.clothes.repository.ClothesRepository;
import codeit.sb06.otboo.exception.user.UserNotFoundException;
import codeit.sb06.otboo.exception.weather.WeatherNotFoundException;
import codeit.sb06.otboo.profile.entity.Profile;
import codeit.sb06.otboo.profile.repository.ProfileRepository;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import codeit.sb06.otboo.weather.dto.weather.PrecipitationType;
import codeit.sb06.otboo.weather.dto.weather.SkyStatus;
import codeit.sb06.otboo.weather.entity.Weather;
import codeit.sb06.otboo.weather.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private static final String DEF_COLOR = "색상";
    private static final String DEF_THICKNESS = "두께감";

    // 후보 풀 크기(점수 계산 대상)
    private static final int TOP_K = 12;

    // 최종 선택 시 상위 N개에서 가중 랜덤
    private static final int PICK_TOP_N = 5;

    // 두께감(날씨 적합도) 가중치: 색상보다 우선하도록 설정
    private static final double W_THICKNESS = 2.0;

    // 색상 조합 점수(상의+하의만) 가중치
    private static final double W_COLOR = 1.0;

    // 속성(두께감) 누락 페널티 기본값
    private static final double MISSING_THICKNESS_BASE_PENALTY = 0.2;

    private final WeatherRepository weatherRepository;
    private final ClothesRepository clothesRepository;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public RecommendationDto getRecommendation(UUID weatherId, UUID userId) {

        Weather weather = weatherRepository.findById(weatherId)
                .orElseThrow(() -> new WeatherNotFoundException(weatherId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        int sensitivity = profileRepository.findByUserId(user)
                .map(Profile::getSensitivity)
                .orElse(3);

        List<Clothes> all = clothesRepository.findAllByOwnerId(userId);

        if (all.isEmpty()) {
            return new RecommendationDto(weather.getId(), userId, List.of());
        }

        RecommendationContext ctx = RecommendationContext.from(weather, sensitivity);

        // 호출마다 다른 결과가 나오도록 랜덤 고정 안 함
        Random rnd = new Random();

        Map<ClothesType, List<Clothes>> byType =
                all.stream().collect(Collectors.groupingBy(Clothes::getType));

        List<Clothes> dresses = byType.getOrDefault(ClothesType.DRESS, List.of());
        List<Clothes> tops = byType.getOrDefault(ClothesType.TOP, List.of());
        List<Clothes> bottoms = byType.getOrDefault(ClothesType.BOTTOM, List.of());
        List<Clothes> outers = byType.getOrDefault(ClothesType.OUTER, List.of());
        List<Clothes> shoes = byType.getOrDefault(ClothesType.SHOES, List.of());
        List<Clothes> hats = byType.getOrDefault(ClothesType.HAT, List.of());
        List<Clothes> accessories = byType.getOrDefault(ClothesType.ACCESSORY, List.of());

        // 메인 착장(원피스 or 상의+하의) 없으면 종료
        boolean hasMain = !dresses.isEmpty() || (!tops.isEmpty() && !bottoms.isEmpty());
        if (!hasMain) {
            return new RecommendationDto(weather.getId(), userId, List.of());
        }

        // 두께감 점수 기준으로 상위 후보 추림
        List<ScoredClothes> topCands = topK(tops, ctx, TOP_K);
        List<ScoredClothes> bottomCands = topK(bottoms, ctx, TOP_K);
        List<ScoredClothes> dressCands = topK(dresses, ctx, TOP_K);
        List<ScoredClothes> outerCands = topK(outers, ctx, TOP_K);
        List<ScoredClothes> shoeCands = topK(shoes, ctx, TOP_K);

        // 루트별 후보를 "여러 개" 만들어서 상위 N개 가중 랜덤으로 고름(다양성)
        List<OutfitCandidate> dressOptions = dressRouteOptions(dressCands, shoeCands, rnd);
        List<OutfitCandidate> separateOptions = separateRouteOptions(topCands, bottomCands, shoeCands, rnd);

        OutfitCandidate chosen = chooseRouteWithDiversity(dressOptions, separateOptions, rnd);

        List<Clothes> result = new ArrayList<>(chosen.items);

        // 맑고 더운 날만 모자 추천
        if (!ctx.isWet()
                && weather.getSkyStatus() == SkyStatus.CLEAR
                && ctx.feltTemp() >= 23
                && rnd.nextDouble() < 0.5
                && !hats.isEmpty()) {
            result.add(randomPick(hats, rnd));
        }

        // 비/눈이면 악세서리 확률 낮춤
        double accProb = ctx.isWet() ? 0.15 : 0.30;
        if (rnd.nextDouble() < accProb && !accessories.isEmpty()) {
            result.add(randomPick(accessories, rnd));
        }

        // 필요할 때만 아우터 추가 (중복 방지, 상위 N개 가중 랜덤)
        if (shouldIncludeOuter(ctx, rnd)
                && !outerCands.isEmpty()
                && result.stream().noneMatch(c -> c.getType() == ClothesType.OUTER)) {

            Clothes outer = pickWeighted(
                    outerCands,
                    Math.min(PICK_TOP_N, outerCands.size()),
                    rnd,
                    0.9
            ).clothes();
            result.add(outer);
        }

        return new RecommendationDto(
                weather.getId(),
                userId,
                result.stream().map(RecommendedClothesDto::from).toList()
        );
    }


    // 단일 아이템 점수 계산
    private double scoreItem(Clothes c, RecommendationContext ctx) {
        Optional<String> thicknessOpt = findAttrValue(c, DEF_THICKNESS);
        int thickness = thicknessOpt.map(this::mapThickness).orElse(2);
        int target = targetThickness(ctx.feltTemp());

        double score = W_THICKNESS * (-Math.abs(thickness - target));

        double missingPenalty = missingThicknessPenalty(ctx.feltTemp());
        if (thicknessOpt.isEmpty()) score -= missingPenalty;

        if (ctx.isWet() && thickness <= 1 && ctx.feltTemp() <= 18) {
            score -= 0.3;
        }

        return score;
    }

    // 두께감 속성 없을 시 페널티
    private double missingThicknessPenalty(double feltTemp) {
        if (feltTemp <= 10 || feltTemp >= 28) return 0.5;
        return MISSING_THICKNESS_BASE_PENALTY;
    }

    // 상의+하의 색 조합 점수
    private double scoreColorCombo(Clothes a, Clothes b) {
        Optional<String> ca = findAttrValue(a, DEF_COLOR);
        Optional<String> cb = findAttrValue(b, DEF_COLOR);
        if (ca.isEmpty() || cb.isEmpty()) return 0.0;

        ColorGroup ga = colorGroup(ca.get());
        ColorGroup gb = colorGroup(cb.get());
        if (ga == ColorGroup.UNKNOWN || gb == ColorGroup.UNKNOWN) return 0.0;

        if (ga == ColorGroup.NEUTRAL && gb == ColorGroup.NEUTRAL) return W_COLOR * 1.0;
        if (ga != gb) return W_COLOR * 1.0;

        return W_COLOR * (-0.3);
    }

    // 체감온도 기준으로 아우터 포함 여부 결정
    private boolean shouldIncludeOuter(RecommendationContext ctx, Random rnd) {
        double t = ctx.feltTemp();

        if (t <= 17) return true;

        if (t <= 22) {
            double p = ctx.isWet() ? 0.70 : 0.40;
            return rnd.nextDouble() < p;
        }

        return false;
    }



    // 원피스 루트 후보 생성
    private List<OutfitCandidate> dressRouteOptions(
            List<ScoredClothes> dresses,
            List<ScoredClothes> shoes,
            Random rnd
    ) {
        if (dresses.isEmpty()) return List.of();

        List<OutfitCandidate> out = new ArrayList<>();

        int dLimit = Math.min(PICK_TOP_N, dresses.size());
        int sLimit = Math.min(PICK_TOP_N, shoes.size());

        // 드레스 상위 N개 각각에 대해 신발을 상위 N 중 가중 랜덤으로 붙여 후보 생성
        for (int i = 0; i < dLimit; i++) {
            ScoredClothes d = dresses.get(i);

            ScoredClothes shoe = null;
            if (!shoes.isEmpty()) {
                shoe = pickWeighted(shoes, sLimit, rnd, 0.9);
            }

            double total = d.score() + (shoe == null ? 0.0 : shoe.score());

            List<Clothes> items = new ArrayList<>();
            items.add(d.clothes());
            if (shoe != null) items.add(shoe.clothes());

            out.add(new OutfitCandidate(true, total, items));
        }

        out.sort(Comparator.comparingDouble(OutfitCandidate::totalScore).reversed());

        return out;
    }

    // 상의 + 하의 루트 후보 생성
    private List<OutfitCandidate> separateRouteOptions(
            List<ScoredClothes> tops,
            List<ScoredClothes> bottoms,
            List<ScoredClothes> shoes,
            Random rnd
    ) {
        if (tops.isEmpty() || bottoms.isEmpty()) return List.of();

        List<OutfitCandidate> out = new ArrayList<>();

        int tLimit = Math.min(PICK_TOP_N, tops.size());
        int bLimit = Math.min(PICK_TOP_N, bottoms.size());
        int sLimit = Math.min(PICK_TOP_N, shoes.size());

        for (int i = 0; i < tLimit; i++) {
            for (int j = 0; j < bLimit; j++) {
                ScoredClothes t = tops.get(i);
                ScoredClothes b = bottoms.get(j);

                double combo = scoreColorCombo(t.clothes(), b.clothes());

                double shoeScore = shoes.isEmpty() ? 0.0 : averageTopNScore(shoes, sLimit);


                ScoredClothes pickedShoe = null;
                if (!shoes.isEmpty()) {
                    pickedShoe = pickWeighted(shoes, sLimit, rnd, 0.9);
                }

                double total = t.score()
                        + b.score()
                        + combo
                        + (pickedShoe == null ? 0.0 : pickedShoe.score());

                List<Clothes> items = new ArrayList<>();
                items.add(t.clothes());
                items.add(b.clothes());
                if (pickedShoe != null) items.add(pickedShoe.clothes());
                out.add(new OutfitCandidate(true, total, items));
            }
        }

        // 점수순 정렬
        out.sort(Comparator.comparingDouble(OutfitCandidate::totalScore).reversed());
        return out;
    }

    // 상위 N개 평균 점수 계산
    private double averageTopNScore(List<ScoredClothes> list, int n) {
        int limit = Math.min(n, list.size());
        if (limit == 0) return 0.0;
        double sum = 0.0;
        for (int i = 0; i < limit; i++) sum += list.get(i).score();
        return sum / limit;
    }



    // 원피스 vs 상하의 루트 중 다양성 고려해 최종 선택
    private OutfitCandidate chooseRouteWithDiversity(
            List<OutfitCandidate> dressOptions,
            List<OutfitCandidate> separateOptions,
            Random rnd
    ) {
        boolean canDress = !dressOptions.isEmpty();
        boolean canSeparate = !separateOptions.isEmpty();

        if (canDress && !canSeparate) {
            return pickWeightedCandidates(dressOptions, rnd);
        }
        if (!canDress && canSeparate) {
            return pickWeightedCandidates(separateOptions, rnd);
        }
        if (!canDress) {
            return OutfitCandidate.unavailable();
        }

        // 루트별 대표 점수 비교후 비슷하면 섞어서 다양성
        double bestDress = dressOptions.get(0).totalScore();
        double bestSep = separateOptions.get(0).totalScore();
        double diff = bestDress - bestSep;

        if (Math.abs(diff) >= 0.3) {
            return diff >= 0 ? pickWeightedCandidates(dressOptions, rnd)
                    : pickWeightedCandidates(separateOptions, rnd);
        }

        boolean chooseDress = rnd.nextDouble() < 0.55;
        return chooseDress ? pickWeightedCandidates(dressOptions, rnd)
                : pickWeightedCandidates(separateOptions, rnd);
    }

    // 상위 후보 중 점수 기반 가중 랜덤 선택
    private OutfitCandidate pickWeightedCandidates(List<OutfitCandidate> options, Random rnd) {
        options.sort(Comparator.comparingDouble(OutfitCandidate::totalScore).reversed());
        int limit = Math.min(PICK_TOP_N, options.size());

        double scale = 0.9;

        double max = options.get(0).totalScore();

        double[] weights = new double[limit];
        double sum = 0.0;
        for (int i = 0; i < limit; i++) {
            double w = Math.exp(scale * (options.get(i).totalScore() - max));
            weights[i] = w;
            sum += w;
        }

        double r = rnd.nextDouble() * sum;
        double acc = 0.0;
        for (int i = 0; i < limit; i++) {
            acc += weights[i];
            if (r <= acc) return options.get(i);
        }
        return options.get(0);
    }

    // 아이템 점수 계산 후 상위 K개 선택
    private List<ScoredClothes> topK(List<Clothes> list, RecommendationContext ctx, int k) {
        if (list == null || list.isEmpty()) return List.of();

        return list.stream()
                .map(c -> new ScoredClothes(c, scoreItem(c, ctx)))
                .sorted(Comparator.comparingDouble(ScoredClothes::score).reversed())
                .limit(k)
                .toList();
    }

    // 상위 N개중 score 기반 가중 랜덤으로 1개 선택
    private ScoredClothes pickWeighted(List<ScoredClothes> list, int n, Random rnd, double scale) {
        int limit = Math.min(n, list.size());
        if (limit == 0) return null;

        double max = list.get(0).score();

        double[] weights = new double[limit];
        double sum = 0.0;
        for (int i = 0; i < limit; i++) {
            double w = Math.exp(scale * (list.get(i).score() - max));
            weights[i] = w;
            sum += w;
        }

        double r = rnd.nextDouble() * sum;
        double acc = 0.0;
        for (int i = 0; i < limit; i++) {
            acc += weights[i];
            if (r <= acc) return list.get(i);
        }
        return list.get(0);
    }

    // 속성 값 조회
    private Optional<String> findAttrValue(Clothes clothes, String defName) {
        if (clothes == null || defName == null) return Optional.empty();

        List<ClothesAttribute> attrs = clothes.getAttributes();
        if (attrs == null || attrs.isEmpty()) return Optional.empty();

        return attrs.stream()
                .filter(a -> a.getDefinition() != null)
                .filter(a -> defName.equals(a.getDefinition().getName()))
                .map(ClothesAttribute::getValue)
                .filter(v -> v != null && !v.isBlank())
                .findFirst()
                .map(String::trim);
    }


    private Clothes randomPick(List<Clothes> list, Random rnd) {
        return list.get(rnd.nextInt(list.size()));
    }

    private int mapThickness(String v) {
        if (v == null) return 2;
        return switch (v.trim()) {
            case "얇음" -> 1;
            case "보통" -> 2;
            case "두꺼움" -> 3;
            default -> 2;
        };
    }

    private int targetThickness(double feltTemp) {
        if (feltTemp >= 26) return 1;
        if (feltTemp >= 18) return 2;
        return 3;
    }

    private enum ColorGroup { NEUTRAL, ACCENT, UNKNOWN }

    private ColorGroup colorGroup(String c) {
        if (c == null) return ColorGroup.UNKNOWN;
        String v = c.trim();

        if (Set.of("검정", "하양", "회색", "베이지", "네이비").contains(v))
            return ColorGroup.NEUTRAL;

        if (Set.of("빨강", "파랑", "초록", "노랑", "보라", "주황", "핑크").contains(v))
            return ColorGroup.ACCENT;

        return ColorGroup.UNKNOWN;
    }



    private record ScoredClothes(
            Clothes clothes,
            double score) {

    }

    private static class OutfitCandidate {
        final boolean available;
        final double totalScore;
        final List<Clothes> items;

        private OutfitCandidate(boolean available, double totalScore, List<Clothes> items) {
            this.available = available;
            this.totalScore = totalScore;
            this.items = items;
        }

        static OutfitCandidate unavailable() {
            return new OutfitCandidate(false, Double.NEGATIVE_INFINITY, List.of());
        }

        double totalScore() {
            return totalScore;
        }
    }

    private record RecommendationContext(
            double tempCurrent,
            double humidity,
            double windSpeed,
            PrecipitationType precipitationType,
            double feltTemp
    ) {
        static RecommendationContext from(Weather w, int sensitivity) {

            double temp = w.getTempCurrent();
            double wind = w.getWindSpeed();
            double hum = w.getHumidity();

            double windPenalty = Math.min(4.0, wind * 0.4);

            double humidityBonus = 0.0;
            if (temp >= 24) {
                humidityBonus = Math.max(0.0, (hum - 60.0)) * 0.03;
                humidityBonus = Math.min(2.0, humidityBonus);
            }

            int s = Math.max(1, Math.min(5, sensitivity)); // 범위 제한
            double sensAdj = (s - 3) * 1.0;

            double felt = temp - windPenalty + humidityBonus + sensAdj;

            return new RecommendationContext(
                    temp,
                    hum,
                    wind,
                    w.getPrecipitationType(),
                    felt
            );
        }

        boolean isWet() {
            return precipitationType != null
                    && precipitationType != PrecipitationType.NONE;
        }
    }
}
