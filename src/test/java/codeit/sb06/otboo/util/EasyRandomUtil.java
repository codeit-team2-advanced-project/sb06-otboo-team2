package codeit.sb06.otboo.util;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;

public class EasyRandomUtil {
    public static EasyRandom getRandom() {
        EasyRandomParameters params = new EasyRandomParameters()
                .excludeField(FieldPredicates.named("id"))
                .stringLengthRange(5, 15)
                .seed(System.currentTimeMillis());      // 랜덤 시드 설정
        return new EasyRandom(params);
    }
}
