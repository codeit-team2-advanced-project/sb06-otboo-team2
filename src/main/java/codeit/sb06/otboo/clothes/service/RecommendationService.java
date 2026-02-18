package codeit.sb06.otboo.clothes.service;

import codeit.sb06.otboo.clothes.dto.ClothesDto;
import codeit.sb06.otboo.clothes.dto.RecommendationDto;
import codeit.sb06.otboo.clothes.entity.Clothes;
import codeit.sb06.otboo.clothes.entity.ClothesType;
import codeit.sb06.otboo.clothes.repository.ClothesRepository;
import codeit.sb06.otboo.weather.dto.weather.SkyStatus;
import codeit.sb06.otboo.weather.entity.Weather;
import codeit.sb06.otboo.weather.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {


    private final WeatherRepository weatherRepository;
    private final ClothesRepository clothesRepository;

    public RecommendationDto getRecommendation(UUID weatherId, UUID userId) {

        Weather weather = weatherRepository.findById(weatherId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 weatherId 입니다."));

        List<Clothes> all = clothesRepository.findAllByOwnerId(userId);

        if (all.isEmpty()) {
            return new RecommendationDto(weather.getId(), userId, List.of());
        }

        Random rnd = new Random(weatherId.getMostSignificantBits() ^ weatherId.getLeastSignificantBits());

        double temp = weather.getTempCurrent();

        // 의상을 타입별로 그룹화
        Map<ClothesType, List<Clothes>> byType =
                all.stream()
                        .collect(Collectors.groupingBy(Clothes::getType));

        List<Clothes> result = new ArrayList<>();

        // 상의 + 하의 or 원피스 중 택1
        boolean chooseOnePiece = false;

        List<Clothes> dresses = byType.getOrDefault(ClothesType.DRESS, List.of());
        List<Clothes> tops = byType.getOrDefault(ClothesType.TOP, List.of());
        List<Clothes> bottoms = byType.getOrDefault(ClothesType.BOTTOM, List.of());

        if (!dresses.isEmpty()) {
            if (temp >= 25) {
                chooseOnePiece = rnd.nextDouble() < 0.7;
            } else {
                chooseOnePiece = rnd.nextDouble() < 0.3;
            }
        }

        if (chooseOnePiece) {
            result.add(randomPick(dresses, rnd));
        } else {
            if (!tops.isEmpty())
                result.add(randomPick(tops, rnd));
            if (!bottoms.isEmpty())
                result.add(randomPick(bottoms, rnd));
        }


        // 아우터 추천
        List<Clothes> outers = byType.getOrDefault(ClothesType.OUTER, List.of());

        boolean includeOuter = false;

        if (temp <= 17) includeOuter = true;
        else if (temp <= 22) includeOuter = rnd.nextDouble() < 0.4;
        else includeOuter = false;

        if (includeOuter && !outers.isEmpty()) {
            result.add(randomPick(outers, rnd));
        }

        // 모자
        List<Clothes> hats = byType.getOrDefault(ClothesType.HAT, List.of());

        if (weather.getSkyStatus() == SkyStatus.CLEAR
                && temp >= 23
                && rnd.nextDouble() < 0.5
                && !hats.isEmpty()) {
            result.add(randomPick(hats, rnd));
        }

        // 악세사리
        List<Clothes> accessories = byType.getOrDefault(ClothesType.ACCESSORY, List.of());

        if (rnd.nextDouble() < 0.3 && !accessories.isEmpty()) {
            result.add(randomPick(accessories, rnd));
        }

        // 신발
        List<Clothes> shoes = byType.getOrDefault(ClothesType.SHOES, List.of());

        if (!shoes.isEmpty()) {
            result.add(randomPick(shoes, rnd));
        }


        return new RecommendationDto(
                weather.getId(),
                userId,
                result.stream().map(ClothesDto::from).toList()
        );
    }

    private Clothes randomPick(List<Clothes> list, Random rnd) {
        return list.get(rnd.nextInt(list.size()));
    }
}
