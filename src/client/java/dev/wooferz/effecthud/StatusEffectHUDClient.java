package dev.wooferz.effecthud;

import dev.wooferz.effecthud.element.EffectHudElement;
import dev.wooferz.hudlib.HudManager;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class StatusEffectHUDClient implements ClientModInitializer {
	public static final String MOD_ID = "status-effect-hud";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	@Override
	public void onInitializeClient() {
		HudManager.registerHudElement(new EffectHudElement());
	}
}