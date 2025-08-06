package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import nl.knokko.customitems.plugin.CustomItemsApi;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;

@SuppressWarnings("unused")
public class EffSpawnProjectile extends Effect {

    static {
        Skript.registerEffect(EffSpawnProjectile.class, "%entity% launches kci %string%");
    }

    private Expression<Entity> shooter;
    private Expression<String> projectileName;

    @Override
    protected void execute(Event event) {
        Entity shooter = this.shooter.getSingle(event);
        String projectileName = this.projectileName.getSingle(event);
        if (shooter instanceof LivingEntity && projectileName != null) {
            CustomItemsApi.launchProjectile((LivingEntity) shooter, projectileName);
        }
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "Let someone launch a custom projectile";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.shooter = (Expression<Entity>) expressions[0];
        this.projectileName = (Expression<String>) expressions[1];
        return true;
    }
}
