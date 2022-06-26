package com.sunekaer.sdrp.config;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class ConfigEntry<T> {
    private @Nullable T value;
    private String comment;

    public ConfigEntry(
            @Nullable T value,
            String comment
    ) {
        this.value = value;
        this.comment = comment;
    }

    public T get() {
        return this.value;
    }

    public @Nullable T value() {
        return value;
    }

    public String comment() {
        return comment;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ConfigEntry) obj;
        return Objects.equals(this.value, that.value) &&
                Objects.equals(this.comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, comment);
    }

    @Override
    public String toString() {
        return "ConfigEntry[" +
                "value=" + value + ", " +
                "comment=" + comment + ']';
    }

}
