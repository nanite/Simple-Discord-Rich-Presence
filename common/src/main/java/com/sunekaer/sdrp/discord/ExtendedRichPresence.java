package com.sunekaer.sdrp.discord;

import com.jagrosh.discordipc.entities.RichPresence;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.OffsetDateTime;

public class ExtendedRichPresence extends RichPresence {
    public @Nullable String button_label_1;
    public @Nullable String button_url_1;
    public @Nullable String button_label_2;
    public @Nullable String button_url_2;

    public ExtendedRichPresence(String state, String details, OffsetDateTime startTimestamp, OffsetDateTime endTimestamp, String largeImageKey, String largeImageText, String smallImageKey, String smallImageText, String partyId, int partySize, int partyMax, String matchSecret, String joinSecret, String spectateSecret, boolean instance) {
        super(state, details, startTimestamp, endTimestamp, largeImageKey, largeImageText, smallImageKey, smallImageText, partyId, partySize, partyMax, matchSecret, joinSecret, spectateSecret, instance);
    }

    public ExtendedRichPresence(String state, String details, OffsetDateTime startTimestamp, OffsetDateTime endTimestamp, String largeImageKey, String largeImageText, String smallImageKey, String smallImageText, String partyId, int partySize, int partyMax, String matchSecret, String joinSecret, String spectateSecret, boolean instance, @Nullable String button_label_1, @Nullable String button_url_1, @Nullable String button_label_2, @Nullable String button_url_2) {
        super(state, details, startTimestamp, endTimestamp, largeImageKey, largeImageText, smallImageKey, smallImageText, partyId, partySize, partyMax, matchSecret, joinSecret, spectateSecret, instance);
        this.button_label_1 = button_label_1;
        this.button_url_1 = button_url_1;
        this.button_label_2 = button_label_2;
        this.button_url_2 = button_url_2;
    }

    @Override
    public JSONObject toJson() {
        var jsonObject = super.toJson();
        var buttonsObj = new JSONArray();
        if (button_label_1 != null && button_url_1 != null) {
            var btn1 = new JSONObject();
            btn1.put("label", button_label_1);
            btn1.put("url", button_url_1);
            buttonsObj.put(btn1);
        }
        if (button_label_2 != null && button_url_2 != null) {
            var btn2 = new JSONObject();
            btn2.put("label", button_label_2);
            btn2.put("url", button_url_2);
            buttonsObj.put(btn2);
        }
        if (!buttonsObj.isEmpty()) {
            jsonObject.put("buttons", buttonsObj);
            if (jsonObject.has("secrets")) {
                jsonObject.remove("secrets");
            }
        }
        return jsonObject;
    }

    public static class ExtendedBuilder {
        private String state;
        private String details;
        private OffsetDateTime startTimestamp;
        private OffsetDateTime endTimestamp;
        private String largeImageKey;
        private String largeImageText;
        private String smallImageKey;
        private String smallImageText;
        private String partyId;
        private int partySize;
        private int partyMax;
        private String matchSecret;
        private String joinSecret;
        private String spectateSecret;
        private boolean instance;

        private String button_label_1;
        private String button_url_1;
        private String button_label_2;
        private String button_url_2;

        public ExtendedBuilder setState(String state) {
            this.state = state;
            return this;
        }

        public ExtendedBuilder setDetails(String details) {
            this.details = details;
            return this;
        }

        public ExtendedBuilder setStartTimestamp(OffsetDateTime startTimestamp) {
            this.startTimestamp = startTimestamp;
            return this;
        }

        public ExtendedBuilder setEndTimestamp(OffsetDateTime endTimestamp) {
            this.endTimestamp = endTimestamp;
            return this;
        }

        public ExtendedBuilder setLargeImage(String largeImageKey, String largeImageText) {
            this.largeImageKey = largeImageKey;
            this.largeImageText = largeImageText;
            return this;
        }

        public ExtendedBuilder setLargeImage(String largeImageKey) {
            return setLargeImage(largeImageKey, null);
        }

        public ExtendedBuilder setSmallImage(String smallImageKey, String smallImageText) {
            this.smallImageKey = smallImageKey;
            this.smallImageText = smallImageText;
            return this;
        }

        public ExtendedBuilder setSmallImage(String smallImageKey) {
            return setSmallImage(smallImageKey, null);
        }

        public ExtendedBuilder setParty(String partyId, int partySize, int partyMax) {
            this.partyId = partyId;
            this.partySize = partySize;
            this.partyMax = partyMax;
            return this;
        }

        public ExtendedBuilder setMatchSecret(String matchSecret) {
            this.matchSecret = matchSecret;
            return this;
        }

        public ExtendedBuilder setJoinSecret(String joinSecret) {
            this.joinSecret = joinSecret;
            return this;
        }

        public ExtendedBuilder setSpectateSecret(String spectateSecret) {
            this.spectateSecret = spectateSecret;
            return this;
        }

        public ExtendedBuilder setInstance(boolean instance) {
            this.instance = instance;
            return this;
        }

        public ExtendedBuilder setButton1(String label, String url) {
            // If the label is greater than 32 characters, it will be truncated.
            if (label.length() > 32) {
                // This is a failsafe
                label = label.substring(0, 32);
            }

            this.button_label_1 = label;
            this.button_url_1 = url;
            return this;
        }

        public ExtendedBuilder setButton2(String label, String url) {
            // If the label is greater than 32 characters, it will be truncated.
            if (label.length() > 32) {
                // This is a failsafe
                label = label.substring(0, 32);
            }

            this.button_label_2 = label;
            this.button_url_2 = url;
            return this;
        }

        public ExtendedRichPresence build() {
            return new ExtendedRichPresence(state, details, startTimestamp, endTimestamp, largeImageKey, largeImageText, smallImageKey, smallImageText, partyId, partySize, partyMax, matchSecret, joinSecret, spectateSecret, instance, button_label_1, button_url_1, button_label_2, button_url_2);
        }
    }
}
