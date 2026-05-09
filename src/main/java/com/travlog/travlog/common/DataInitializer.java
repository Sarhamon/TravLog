package com.travlog.travlog.common;

import com.travlog.travlog.history.History;
import com.travlog.travlog.history.HistoryRepository;
import com.travlog.travlog.history.HistoryStatus;
import com.travlog.travlog.plan.Plan;
import com.travlog.travlog.plan.PlanRepository;
import com.travlog.travlog.schedule.Schedule;
import com.travlog.travlog.schedule.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@Profile("dev")
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    public ApplicationRunner sampleDataLoader(PlanRepository planRepository,
                                              ScheduleRepository scheduleRepository,
                                              HistoryRepository historyRepository) {
        return args -> {
            if (planRepository.count() > 0) {
                return;
            }

            LocalDate today = LocalDate.now();

            Plan ongoing = planRepository.save(new Plan(
                    "오사카 미식 여행",
                    "일본 오사카",
                    today.minusDays(1),
                    today.plusDays(2),
                    600_000L,
                    "도톤보리 중심으로 야식 위주"
            ));
            Schedule ongoingDay1 = scheduleRepository.save(new Schedule(
                    ongoing, today.minusDays(1), "도톤보리", LocalTime.of(19, 0), "타코야키 / 오코노미야키"
            ));
            scheduleRepository.save(new Schedule(
                    ongoing, today, "오사카성", LocalTime.of(10, 0), "공원 산책"
            ));
            scheduleRepository.save(new Schedule(
                    ongoing, today.plusDays(1), "유니버설 스튜디오", LocalTime.of(9, 0), "조조 입장"
            ));
            historyRepository.save(new History(
                    ongoingDay1, HistoryStatus.DONE, "도톤보리", LocalTime.of(19, 30), 45_000L, "사람 많았지만 만족"
            ));

            Plan upcoming = planRepository.save(new Plan(
                    "제주 가족여행",
                    "제주도",
                    today.plusDays(14),
                    today.plusDays(17),
                    1_200_000L,
                    "렌트카 + 동쪽 코스"
            ));
            scheduleRepository.save(new Schedule(
                    upcoming, today.plusDays(14), "성산일출봉", LocalTime.of(7, 0), "일출 시간 확인 필요"
            ));
            scheduleRepository.save(new Schedule(
                    upcoming, today.plusDays(15), "우도", LocalTime.of(11, 0), "배편 예약"
            ));

            Plan past = planRepository.save(new Plan(
                    "강릉 주말여행",
                    "강원도 강릉",
                    today.minusDays(20),
                    today.minusDays(18),
                    300_000L,
                    "바다 + 카페"
            ));
            Schedule pastDay1 = scheduleRepository.save(new Schedule(
                    past, today.minusDays(20), "안목해변", LocalTime.of(15, 0), "커피거리 산책"
            ));
            Schedule pastDay2 = scheduleRepository.save(new Schedule(
                    past, today.minusDays(19), "정동진", LocalTime.of(6, 0), "일출 보기"
            ));
            Schedule pastDay3 = scheduleRepository.save(new Schedule(
                    past, today.minusDays(18), "오죽헌", LocalTime.of(10, 0), "역사 코스"
            ));
            historyRepository.save(new History(
                    pastDay1, HistoryStatus.DONE, "안목해변", LocalTime.of(15, 30), 18_000L, "날씨 흐렸지만 분위기 좋음"
            ));
            historyRepository.save(new History(
                    pastDay2, HistoryStatus.SKIPPED, null, null, null, "비 와서 못 감"
            ));
            historyRepository.save(new History(
                    pastDay3, HistoryStatus.CHANGED, "참소리축음기박물관", LocalTime.of(11, 0), 12_000L, "오죽헌 대신 방문"
            ));

            log.info("샘플 데이터 로드 완료 (Plan {}, Schedule {}, History {})",
                    planRepository.count(), scheduleRepository.count(), historyRepository.count());
        };
    }
}
