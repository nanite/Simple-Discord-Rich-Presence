package com.sunekaer.sdrp.config;

import com.jagrosh.discordipc.entities.RichPresence;
import com.sunekaer.sdrp.SDRP;
import com.sunekaer.sdrp.discord.State;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import java.util.ArrayList;
import java.util.List;

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
- {{dimension.path}}: The dimension identifier (e.g. overworld) excluding the namespace
- {{dimension.namespace}}: The dimension namespace (e.g. minecraft, modid)
- {{dimension.identifier}}: The full dimension identifier (e.g. minecraft:overworld)
- {{player.uuid}}: The player's UUID
- {{player.name}}: The player's in-game name
- Please let us know if there are any other variables you'd like to see added!
    """)
    public List<DimensionEntry> dimensionsSupport = new ArrayList<>() {{
        add(new DimensionEntry(
                "exact@minecraft:overworld",
                "sdrp.{{dimension.path}}.in",
                "{{dimension.path}}",
                "{{dimension.path}}"
        ));
        add(new DimensionEntry(
                "exact@minecraft:the_nether",
                "sdrp.{{dimension.path}}.in",
                "{{dimension.path}}",
                "{{dimension.path}}"
        ));
        add(new DimensionEntry(
                "exact@minecraft:the_end",
                "sdrp.{{dimension.path}}.in",
                "{{dimension.path}}",
                "{{dimension.path}}"
        ));
    }};

    @Comment("If set to false, it disables the build-in screenEvent, which is used to tell when we are on the main menu, or other enabled screens.")
    public boolean enableUpdateScreenPresence = true;

    @Comment("Screens")
    public List<ScreenTranslation> screens = new ArrayList<>() {{
        add(new ScreenTranslation(
                List.of(
                        "net.minecraft.client.gui.screens.TitleScreen",
                        "net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen",
                        "net.minecraft.client.gui.screens.worldselection.SelectWorldScreen"
                ),
                "sdrp.mainmenu",
                "sdrp.mainmenu",
                "menu"
        ));

    }};

    public static final class Button {
        public String label;
        public String url;
    }

    public static final class ScreenTranslation extends State {
        public List<String> screenClass;

        public ScreenTranslation(List<String> screenClass, String message, String imageName, String imageKey) {
            super(message, imageName, imageKey);
            this.screenClass = screenClass;
        }
    }

    public record DimensionEntry(
            String matcher,
            String message,
            String imageName,
            String imageKey
    ) {
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
                    ResourceLocation rl = ResourceLocation.tryParse(compareTo);
                    yield rl != null && rl.getNamespace().equals(matchValue);
                }
                case "path" -> {
                    ResourceLocation rl = ResourceLocation.tryParse(compareTo);
                    yield rl != null && rl.getPath().equals(matchValue);
                }
                default -> false;
            };
        }

        public RichPresence createPresence(ResourceLocation dimensionName, Player player) {
            return new State(
                    processVariables(message, dimensionName, player),
                    processVariables(imageName, dimensionName, player),
                    processVariables(imageKey, dimensionName, player)
            ).createPresence();
        }

        public static String processVariables(String template, ResourceLocation dimensionName, Player player) {
            return template
                    .replace("{{dimension.identifier}}", dimensionName.toString())
                    .replace("{{dimension.namespace}}", dimensionName.getNamespace())
                    .replace("{{dimension.path}}", dimensionName.getPath())
                    .replace("{{player.uuid}}", player.getUUID().toString())
                    .replace("{{player.name}}", player.getName().getString());
        }
    }
}
