package dev.wooferz.effecthud.element;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.impl.controller.TickBoxControllerBuilderImpl;
import dev.wooferz.effecthud.StatusEffectHUD;
import dev.wooferz.effecthud.config.EffectHudConfig;
import dev.wooferz.hudlib.HudAnchor;
import dev.wooferz.hudlib.hud.HUDConfig;
import dev.wooferz.hudlib.hud.HUDElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.Color;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static dev.wooferz.effecthud.StatusEffectHUDClient.disableVanillaGui;

public class EffectHudElement extends HUDElement {

    public EffectHudConfig config = new EffectHudConfig();
    boolean editorOpen = false;

    public EffectHudElement() {
        super("Status Effect HUD", 10, 0, 120, 29, 1, StatusEffectHUD.MOD_ID, "effect-hud",  HudAnchor.HorizontalAnchor.LEFT, HudAnchor.VerticalAnchor.MIDDLE);
    }

    @Override
    public Integer getHeight() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player != null) {

            Collection<StatusEffectInstance> effects = player.getStatusEffects();
            if (effects.isEmpty()) {
                return 29;
            }
            return effects.size() * 25 + 4;
        }
        return 40;
    }

    @Override
    public void render(int x, int y, int width, int height, DrawContext drawContext, float v) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        disableVanillaGui = this.config.disableVanillaHud;


        if (player != null) {

            int color = (config.color.getAlpha() << 24) | (config.color.getRed() << 16) | (config.color.getGreen() << 8) | config.color.getBlue();
            int bgColor = (config.bgColor.getAlpha() << 24) | (config.bgColor.getRed() << 16) | (config.bgColor.getGreen() << 8) | config.bgColor.getBlue();
            int timeColor = (config.timeColor.getAlpha() << 24) | (config.timeColor.getRed() << 16) | (config.timeColor.getGreen() << 8) | config.timeColor.getBlue();

            int i = 0;
            Collection<StatusEffectInstance> effects = player.getStatusEffects();
            if (effects.isEmpty()) {
                if (editorOpen) {
                    effects = new ArrayList<StatusEffectInstance>();
                    effects.add(new StatusEffectInstance(StatusEffects.STRENGTH, 120, 1));
                } else {
                    return;
                }
            }

            int rHeight = effects.size()*25+4;
            drawContext.fill(x, y, x + width, y+rHeight, bgColor);

            for (StatusEffectInstance effect : effects) {
                StatusEffect effectType = effect.getEffectType();
                String registryName = String.valueOf(Registries.STATUS_EFFECT.getId(effectType)).replaceFirst("^minecraft:", "");
                Identifier texture = Identifier.of("minecraft", "textures/mob_effect/" + registryName + ".png");

                String amplifier = " " + (effect.getAmplifier() + 1);


                drawContext.drawText(mc.textRenderer, Text.translatable(effectType.getTranslationKey()).getString() + amplifier, 2+x+19, 3+2+y+i*25, color, true);
                if (effect.getDuration() != StatusEffectInstance.INFINITE) {
                    drawContext.drawText(mc.textRenderer, String.format(
                            "%02d:%02d:%02d",
                            effect.getDuration() / 20 / 60 / 60,
                            effect.getDuration() / 20 / 60 % 60,
                            effect.getDuration() / 20 % 60
                    ), 2+x+19, 3+14+y+i*25, timeColor, true);
                } else {
                    drawContext.drawText(mc.textRenderer, "∞", 2+x+19, 3+14+y+i*25, timeColor, true);
                }


                drawContext.drawTexture(texture, 2+x, 7+y+i*25, 0, 0, 16, 16, 16, 16);


                i++;
            }


        }
    }

    @Override
    public void editorOpened() {
        editorOpen = true;
    }

    @Override
    public void editorClosed() {
        editorOpen = false;
    }

    @Override
    public Class<?> getConfigType() {
        return EffectHudConfig.class;
    }

    @Override
    public HUDConfig getConfig() {
        return config;
    }

    @Override
    public void setConfig(HUDConfig config) {
        if (config != null) {
            if (config instanceof EffectHudConfig) {
                this.config = (EffectHudConfig) config;
                disableVanillaGui = this.config.disableVanillaHud;
            }
        }
    }

    @Override
    public OptionGroup generateConfig() {
        OptionGroup optionGroup = OptionGroup.createBuilder()
                .name(Text.literal(displayName))
                .option(Option.<Boolean>createBuilder()
                        .name(Text.literal("Disable Vanilla Effect HUD"))
                        .binding(false,
                                () -> config.disableVanillaHud,
                                value -> config.disableVanillaHud = value)
                        .controller(TickBoxControllerBuilderImpl::new)
                        .build())
                .description(OptionDescription.of(Text.literal("This will work even when Status Effect HUD is disabled.")))
                .option(Option.<Color>createBuilder()
                        .name(Text.literal("Text Color"))
                        .binding(Color.WHITE,
                                () -> config.color,
                                newColor -> config.color = newColor)
                        .controller(ColorControllerBuilder::create)
                        .build()
                )
                .option(Option.<Color>createBuilder()
                        .name(Text.literal("Time Color"))
                        .binding(Color.GRAY,
                                () -> config.timeColor,
                                newColor -> config.timeColor = newColor)
                        .controller(ColorControllerBuilder::create)
                        .build()
                )
                .option(Option.<Color>createBuilder()
                        .name(Text.literal("Background Color"))
                        .binding(new Color(0x99000000, true),
                                () -> config.bgColor,
                                newColor -> config.bgColor = newColor)
                        .controller(opt -> ColorControllerBuilder.create(opt)
                                .allowAlpha(true))
                        .build()
                )
                .build();
        return optionGroup;
    }
}
