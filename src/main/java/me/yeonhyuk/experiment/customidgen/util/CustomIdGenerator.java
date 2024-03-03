package me.yeonhyuk.experiment.customidgen.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class CustomIdGenerator {

    @Value("${id_generator.datacenter_id}")
    private static long datacenterId;

    @Value("${id_generator.server_id}")
    private static long serverId;

    private final static long START_STMP = 1707828242000L;

    private final static long SIGN_BIT = 1;
    private final static long YEAR_BIT = 14;
    private final static long CHAT_ROOM_ID_BIT = 40;
    private final static long TIMESTAMP_ID_BIT = 51;
    private final static long DATACENTER_ID_BIT = 5;
    private final static long SERVER_ID_BIT = 5;
    private final static long SEQUENCE_BIT = 12;

    private final static long MAX_YEAR_BIT = ~(-1 << YEAR_BIT);
    private final static long MAX_CHAT_ROOM_ID_BIT = ~(-1 << CHAT_ROOM_ID_BIT);
    private final static long MAX_TIMESTAMP_ID_BIT = ~(-1 << TIMESTAMP_ID_BIT);
    private final static long MAX_DATACENTER_ID_BIT = ~(-1 << DATACENTER_ID_BIT);
    private final static long MAX_SERVER_ID_BIT = ~(-1 << SERVER_ID_BIT);
    private final static long MAX_SEQUENCE_BIT = ~(-1 << SEQUENCE_BIT);

    private final static long SERVER_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SERVER_LEFT + SERVER_ID_BIT;
    private final static long TIMESTAMP_LEFT = DATACENTER_LEFT + DATACENTER_ID_BIT;
    private final static long CHAT_ROOM_LEFT = TIMESTAMP_LEFT + TIMESTAMP_ID_BIT;
    private final static long YEAR_LEFT = CHAT_ROOM_LEFT + CHAT_ROOM_ID_BIT;

    private long DATACENTER_ID_SHIFTED;
    private long SERVER_ID_SHIFTED;

    private long lastStamp = -1;
    private long sequence = 0;

    @PostConstruct
    public void postConstruct() {
        if(datacenterId > MAX_DATACENTER_ID_BIT || datacenterId < 0) {
            throw new RuntimeException();
        } else {
            DATACENTER_ID_SHIFTED = datacenterId << DATACENTER_LEFT;
        }
        if(serverId > MAX_SERVER_ID_BIT || serverId < 0) {
            throw new RuntimeException();
        } else {
            SERVER_ID_SHIFTED = serverId << SERVER_LEFT;
        }
    }

    public long getNewId(long chatRoomId) {
        long instanceId = this.getTemporaryId();
        return this.getFullId(instanceId, chatRoomId);
    }

    public long getFullId(long temporaryId, long chatRoomId) {
        if (chatRoomId > MAX_CHAT_ROOM_ID_BIT || chatRoomId < 0) {
            throw new RuntimeException();
        }
        long idDatetime = getTimeStampFromId(temporaryId);
        Instant instant = Instant.ofEpochMilli(idDatetime);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));

        long year = (long) localDateTime.getYear();

        return (year << YEAR_LEFT) | (chatRoomId << CHAT_ROOM_LEFT) | temporaryId;
    }

    public long getTemporaryId() {
        return ((this.getCurrStmp() - START_STMP) << TIMESTAMP_LEFT) | DATACENTER_ID_SHIFTED | SERVER_ID_SHIFTED | this.sequence;
    }

    public long getCurrStmp() {
        long currTimestamp = System.currentTimeMillis();
        if (currTimestamp < this.lastStamp) {
            throw new RuntimeException();
        }
        if (currTimestamp == this.lastStamp) {
            this.sequence = (this.sequence + 1) & MAX_SEQUENCE_BIT;
            if (sequence == 0) {
                currTimestamp = this.getNextMill();
            }
        } else {
            this.sequence = 0;
        }
        this.lastStamp = currTimestamp;
        return currTimestamp;
    }

    public long getNextMill() {
        long currentTimestamp = System.currentTimeMillis();
        while (currentTimestamp <= this.lastStamp) {
            System.out.println("delay!!!!!!!!");
            currentTimestamp = System.currentTimeMillis();
        }
        return currentTimestamp;
    }

    public static long getTimeStampFromId(long customId) {
        long mask = getMask(TIMESTAMP_ID_BIT, TIMESTAMP_LEFT);
        return START_STMP + ((customId & mask) >> TIMESTAMP_LEFT);
    }

    public static long getMask(long numOfBits, long numOfLeft) {
        return ((long) Math.pow(2, numOfBits) - 1) << numOfLeft;
    }

    public static long getYear(long customId) {
        return customId & getMask(YEAR_BIT, YEAR_LEFT);
    }

    public static long getChatRoomId(long customId) {
        return customId & getMask(CHAT_ROOM_ID_BIT, CHAT_ROOM_LEFT);
    }
}
