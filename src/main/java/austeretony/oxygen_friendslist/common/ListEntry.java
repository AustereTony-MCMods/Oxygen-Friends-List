package austeretony.oxygen_friendslist.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import austeretony.oxygen_core.common.persistent.PersistentEntry;
import austeretony.oxygen_core.common.sync.SynchronousEntry;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_core.common.util.StreamUtils;
import io.netty.buffer.ByteBuf;

public class ListEntry implements PersistentEntry, SynchronousEntry {

    public static final int MAX_NOTE_LENGTH = 80;

    private long id;

    private EnumEntryType type;

    private UUID playerUUID;

    private String note = StringUtils.EMPTY;

    public ListEntry() {}

    public ListEntry(long id, EnumEntryType type, UUID playerUUID) {
        this.id = id;
        this.type = type;
        this.playerUUID = playerUUID;
    }

    @Override
    public long getId() {
        return this.id;
    }

    public void setId(long entryId) {
        this.id = entryId;
    }

    public EnumEntryType getType() {
        return this.type;
    }

    public void setType(EnumEntryType type) {
        this.type = type;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write((byte) this.type.ordinal(), bos);
        StreamUtils.write(this.playerUUID, bos);
        StreamUtils.write(this.id, bos);
        StreamUtils.write(this.note, bos);
    }

    @Override
    public void read(BufferedInputStream bis) throws IOException {
        this.type = EnumEntryType.values()[StreamUtils.readByte(bis)];
        this.playerUUID = StreamUtils.readUUID(bis);
        this.id = StreamUtils.readLong(bis);
        this.note = StreamUtils.readString(bis);
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeByte(this.type.ordinal());
        ByteBufUtils.writeUUID(this.playerUUID, buffer);
        buffer.writeLong(this.id);
        ByteBufUtils.writeString(this.note, buffer);
    }

    @Override
    public void read(ByteBuf buffer) {
        this.type = EnumEntryType.values()[buffer.readByte()];
        this.playerUUID = ByteBufUtils.readUUID(buffer);
        this.id = buffer.readLong();
        this.note = ByteBufUtils.readString(buffer);
    }

    public enum EnumEntryType {

        FRIEND,
        IGNORED
    }
}
