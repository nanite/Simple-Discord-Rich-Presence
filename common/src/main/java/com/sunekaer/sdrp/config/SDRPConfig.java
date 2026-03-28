package com.sunekaer.sdrp.config;

import com.jagrosh.discordipc.entities.RichPresence;
import com.sunekaer.sdrp.SDRP;
import com.sunekaer.sdrp.discord.State;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Config(name = SDRP.MOD_ID + "-common")
public class SDRPConfig implements ConfigData {
    @Comment("Your Discord App ID")
    public long clientId = 608012526537408579L;

    @Comment("If you wish to disable Discord Rich Presence, set this to false.")
    public boolean enabled = true;

    @Comment("When enabled, the mod will log the current state being sent to Discord")
    public boolean logState = false;

    @Comment("""
            Set custom buttons for the Discord Rich Presences. You can only have 2 buttons, each button has a label and a URL.
            
            Example:
            [
                {
                    "label": "Join our Discord!",
                    "url": "https://discord.gg/...
                },
                ...
            ]
            """)
    public List<Button> buttons = new ArrayList<>();

    @Comment("If set to false, it disables the build-in clientJoinEvent, which is used to tell when the player joins a world and changing Dimension.")
    public boolean enableUpdateDimensionPresence = true;

    @Comment("""
            Dimensions can be setup to update the Rich Presence when the player is in them.
            
            Due to the complex nature of modded dimensions, we've added support for different matchers here with 
            support for variable replacement in the various fields.
            
            Note: When comparing, the full dimension identifier is used (e.g. minecraft:overworld, modid:custom_dimension) meaning if you use 
            a startsWith, regex, etc, you need to handle the namespace as well. 
            
            Helper matchers are provided for doing blanket matches on just the namespace or path.
            
            Supported matchers:
            - exact@<string> : Exact match
            - contains@<string> : Contains substring
            - startsWith@<string> : Starts with substring
            - endsWith@<string> : Ends with substring
            - regex@<pattern> : Matches regex pattern (Java regex syntax)
            - namespace@<string> : Matches namespace
            - path@<string> : Matches path
            
            Variables:
            - {{dimension.name}}: The translated dimension name (e.g. dimension.minecraft.overworld = Overworld). This is NeoForge standards, not all mods follow this, nor will all have their own lang keys added. 
            - {{dimension.path}}: The dimension identifier (e.g. overworld) excluding the namespace
            - {{dimension.namespace}}: The dimension namespace (e.g. minecraft, modid)
            - {{dimension.identifier}}: The full dimension identifier (e.g. minecraft:overworld)
            - {{player.uuid}}: The player's UUID
            - {{player.name}}: The player's in-game name
            - Please let us know if there are any other variables you'd like to see added!
            """)
    public List<DimensionEntry> dimensionsSupport = new ArrayList<>();

    @Comment("If set to false, it disables the build-in screenEvent, which is used to tell when we are on the main menu, or other enabled screens.")
    public boolean enableUpdateScreenPresence = true;

    @Comment("""
            Screens can be setup to update the Rich Presence when the player is on them. You can specify multiple screen classes for each entry. 
            Each screen should have a message, imageName and imageKey.
            
            Example:    
            [
                {
                    "screenClass": [
                        "net.minecraft.client.gui.screens.TitleScreen"
                    ],
                    "message": "sdrp.mainmenu",
                    "imageName": "sdrp.mainmenu",
                    "imageKey": "menu"
                },
                ...
            ]
            
            This would update the Rich Presence when on the Title Screen to have the message and images specified.
            """)
    public List<ScreenTranslation> screens = new ArrayList<>();

    @Override
    public void validatePostLoad() throws ValidationException {
        if (this.screens.isEmpty()) {
            this.screens.add(new ScreenTranslation(
                    List.of(
                            "net.minecraft.client.gui.screens.TitleScreen",
                            "net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen",
                            "net.minecraft.client.gui.screens.worldselection.SelectWorldScreen"
                    ),
                    "sdrp.mainmenu",
                    "sdrp.mainmenu",
                    "menu"
            ));
        }

        if (this.dimensionsSupport.isEmpty()) {
            this.dimensionsSupport.addAll(List.of(new DimensionEntry(
                            "exact@minecraft:overworld",
                            "{{dimension.name}}",
                            "{{dimension.name}}",
                            "{{dimension.path}}"
                    ),
                    new DimensionEntry(
                            "exact@minecraft:the_nether",
                            "{{dimension.name}}",
                            "{{dimension.name}}",
                            "{{dimension.path}}"
                    ),
                    new DimensionEntry(
                            "exact@minecraft:the_end",
                            "{{dimension.name}}",
                            "{{dimension.name}}",
                            "{{dimension.path}}"
                    )
            ));
        }
    }

    public static final class Button {
        public String label;
        public String url;
    }

    public static final class ScreenTranslation extends State {
        public List<String> screenClass;

        public ScreenTranslation() {
            super("", "", "");
            this.screenClass = new ArrayList<>();
        }

        public ScreenTranslation(List<String> screenClass, String message, String imageName, String imageKey) {
            super(message, imageName, imageKey);
            this.screenClass = screenClass;
        }
    }

    public static final class DimensionEntry {
        public String matcher;
        public String message;
        public String imageName;
        public String imageKey;
        private final boolean prefixWithIn;

        public DimensionEntry() {
            this.prefixWithIn = true;
        }

        public DimensionEntry(
                String matcher,
                String message,
                String imageName,
                String imageKey,
                boolean prefixWithIn
        ) {
            this.matcher = matcher;
            this.message = message;
            this.imageName = imageName;
            this.imageKey = imageKey;
            this.prefixWithIn = prefixWithIn;
        }

        public DimensionEntry(String matcher, String message, String imageName, String imageKey) {
            this(matcher, message, imageName, imageKey, true);
        }

        /**
         * Determine if the given string matches the matcher
         * <p>
         * Supported matchers:
         * - exact@<string> : Exact match
         * - contains@<string> : Contains substring
         * - startsWith@<string> : Starts with substring
         * - endsWith@<string> : Ends with substring
         * - namespace@<string> : Matches namespace
         * - path@<string> : Matches path
         * - regex@<pattern> : Matches regex pattern
         *
         * @param compareTo The string to compare to
         * @return True if it matches, false otherwise
         */
        public boolean matches(String compareTo) {
            // Just default to exact match if no prefix is found
            if (!matcher.contains("@")) {
                return matcher.equals(compareTo);
            }

            String[] parts = matcher.split("@", 2);
            String matchType = parts[0];
            String matchValue = parts[1];

            return switch (matchType) {
                case "exact" -> compareTo.equals(matchValue);
                case "contains" -> compareTo.contains(matchValue);
                case "startsWith" -> compareTo.startsWith(matchValue);
                case "endsWith" -> compareTo.endsWith(matchValue);
                case "regex" -> compareTo.matches(matchValue);
                case "namespace" -> {
                    Identifier rl = Identifier.tryParse(compareTo);
                    yield rl != null && rl.getNamespace().equals(matchValue);
                }
                case "path" -> {
                    Identifier rl = Identifier.tryParse(compareTo);
                    yield rl != null && rl.getPath().equals(matchValue);
                }
                default -> false;
            };
        }

        public RichPresence createPresence(Identifier dimensionName, Player player) {
            var msg = processVariables(message, dimensionName, player);
            if (prefixWithIn) {
                // If we prefix, we need to translate here, then pass it down into the msg. This will try and translate again and fail, but we'll already have the correct string.
                msg = I18n.get("sdrp.in") + " " + I18n.get(msg);
            }

            return new State(
                    msg,
                    processVariables(imageName, dimensionName, player),
                    processVariables(imageKey, dimensionName, player)
            ).createPresence();
        }

        public static String processVariables(String template, Identifier dimensionName, Player player) {
            String langKey = dimensionName.toLanguageKey("dimension");
            String translated = I18n.get(langKey);
            if (translated.equals(langKey)) {
                translated = prettifyName(dimensionName.getPath());
            }

            return template
                    .replace("{{dimension.name}}", translated)
                    .replace("{{dimension.identifier}}", dimensionName.toString())
                    .replace("{{dimension.namespace}}", dimensionName.getNamespace())
                    .replace("{{dimension.path}}", dimensionName.getPath())
                    .replace("{{player.uuid}}", player.getUUID().toString())
                    .replace("{{player.name}}", player.getName().getString());
        }

        private static String prettifyName(String path) {
            String[] parts = path.split("_");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) sb.append(' ');
                if (!parts[i].isEmpty()) {
                    sb.append(Character.toUpperCase(parts[i].charAt(0)));
                    sb.append(parts[i].substring(1));
                }
            }
            return sb.toString();
        }

        public String matcher() {
            return matcher;
        }

        public String message() {
            return message;
        }

        public String imageName() {
            return imageName;
        }

        public String imageKey() {
            return imageKey;
        }

        public boolean prefixWithIn() {
            return prefixWithIn;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (DimensionEntry) obj;
            return Objects.equals(this.matcher, that.matcher) &&
                    Objects.equals(this.message, that.message) &&
                    Objects.equals(this.imageName, that.imageName) &&
                    Objects.equals(this.imageKey, that.imageKey) &&
                    this.prefixWithIn == that.prefixWithIn;
        }

        @Override
        public int hashCode() {
            return Objects.hash(matcher, message, imageName, imageKey, prefixWithIn);
        }

        @Override
        public String toString() {
            return "DimensionEntry[" +
                    "matcher=" + matcher + ", " +
                    "message=" + message + ", " +
                    "imageName=" + imageName + ", " +
                    "imageKey=" + imageKey + ", " +
                    "prefixWithIn=" + prefixWithIn + ']';
        }

    }
}
