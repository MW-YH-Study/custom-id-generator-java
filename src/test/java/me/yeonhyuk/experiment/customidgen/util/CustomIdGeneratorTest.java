package me.yeonhyuk.experiment.customidgen.util;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class CustomIdGeneratorTest {

    @Autowired
    CustomIdGenerator customIdGenerator;

    // 36999400
    @Test
    void generateUuid() {
        Timer timer = new Timer();
        timer.start();
        for (int i = 0; i < 10000; i++) {
            UUID newUuid = UUID.randomUUID();
        }
        timer.end();
        timer.printResult();
    }

    @Test
    void getFullId() {
    }

    @Test
    void getTemporaryId() {
        Timer timer = new Timer();
        timer.start();
        long newId = customIdGenerator.getTemporaryId();
        timer.end();
        System.out.println("Created new Id: " + newId);
        timer.printResult();
    }

    // 2000000
    @Test
    void generateTemporaryId() {
        Timer timer = new Timer();
        timer.start();
        for (int i = 0; i < 10000; i++) {
            long newId = customIdGenerator.getTemporaryId();
        }
        timer.end();
        timer.printResult();
    }

    @Test
    void getTimeStampFromId() {
    }

    @Test
    void getYear() {
    }

    @Test
    void getChatRoomId() {
    }
}