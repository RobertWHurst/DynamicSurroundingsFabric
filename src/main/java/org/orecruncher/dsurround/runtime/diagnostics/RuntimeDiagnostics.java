package org.orecruncher.dsurround.runtime.diagnostics;

import com.google.common.collect.ImmutableList;
import joptsimple.internal.Strings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Formatting;
import org.orecruncher.dsurround.eventing.ClientEventHooks;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.MinecraftClock;
import org.orecruncher.dsurround.lib.math.TimerEMA;
import org.orecruncher.dsurround.runtime.ConditionEvaluator;

import java.util.Collection;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class RuntimeDiagnostics {

    private static final MinecraftClock clock = new MinecraftClock();

    private static List<String> scripts = ImmutableList.of(
            "'Dim: ' + dim.getId() + '/' + dim.getDimName() + '; isSuperFlat: ' + dim.isSuperFlat()",
            "'Biome: ' + biome.getName() + '; Temp ' + biome.getTemperature() + '; rainfall: ' + biome.getRainfall() + '; category: ' + biome.getCategory()",
            "'Weather: ' + lib.iif(weather.isRaining(),'rain: ' + weather.getRainIntensity(),'not raining') + lib.iif(weather.isThundering(),' thundering','') + '; Temp: ' + weather.getTemperature() + '; ice: ' + lib.iif(weather.getTemperature() < 0.15, 'true', 'false') + ' ' + lib.iif(weather.getTemperature() < 0.2, '(breath)', '')",
            "'Diurnal: ' + lib.iif(diurnal.isNight(),' night,',' day,') + '; celestial angle: ' + diurnal.getCelestialAngle()",
            "'Player: health ' + player.getHealth() + '/' + player.getMaxHealth() + '; food ' + player.getFoodLevel() + '/' + player.getFoodSaturationLevel() + '; pos (' + player.getX() + ', ' + player.getY() + ', ' + player.getZ() + ')'"
    );

    public static void register() {
        ClientEventHooks.COLLECT_DIAGNOSTICS.register(RuntimeDiagnostics::onCollect);
    }

    private static void onCollect(Collection<String> left, Collection<String> right, Collection<TimerEMA> timers) {
        if (GameUtils.isInGame()) {
            clock.update(GameUtils.getWorld());
            left.add(Formatting.GREEN + clock.getFormattedTime());
            left.add(Strings.EMPTY);

            for (String script : scripts) {
                Object result = ConditionEvaluator.INSTANCE.eval(script);
                left.add(Formatting.YELLOW + result.toString());
            }
        }
    }
}