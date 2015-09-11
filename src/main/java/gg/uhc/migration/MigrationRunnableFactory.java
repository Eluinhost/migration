package gg.uhc.migration;

import com.google.common.base.Preconditions;
import gg.uhc.migration.messages.MessageSender;
import gg.uhc.migration.selection.AreaSelection;

import java.util.List;

public class MigrationRunnableFactory {

    protected List<Area> potentialAreas = null;

    protected Integer warningPhaseSeconds = null;
    protected Integer damagePhaseSeconds = null;
    protected Integer secondsForHalfHeart = null;

    protected MessageSender phaseStartSender = null;
    protected MessageSender updatesSender = null;
    protected MessageSender damageSender = null;

    protected List<Long> notificationTicks = null;
    protected PlayerStrings playerStrings = null;

    protected MigrationRunnableFactory areas(List<Area> areas) {
        this.potentialAreas = areas;
        return this;
    }

    protected MigrationRunnableFactory warningPhaseTime(int seconds) {
        this.warningPhaseSeconds = seconds;
        return this;
    }

    protected MigrationRunnableFactory damagePhaseTime(int seconds) {
        this.damagePhaseSeconds = seconds;
        return this;
    }

    protected MigrationRunnableFactory secondsPerHalfHeart(int seconds) {
        this.secondsForHalfHeart = seconds;
        return this;
    }

    protected MigrationRunnableFactory phaseStartSender(MessageSender sender) {
        this.phaseStartSender = sender;
        return this;
    }

    protected MigrationRunnableFactory updatesSender(MessageSender sender) {
        this.updatesSender = sender;
        return this;
    }

    protected MigrationRunnableFactory damageSender(MessageSender sender) {
        this.damageSender = sender;
        return this;
    }

    protected MigrationRunnableFactory notificationTicks(List<Long> notificationTicks) {
        this.notificationTicks = notificationTicks;
        return this;
    }

    protected MigrationRunnableFactory playerStrings(PlayerStrings strings) {
        this.playerStrings = strings;
        return this;
    }

    public MigrationRunnable createWithSelector(AreaSelection selection) {
        Preconditions.checkState(warningPhaseSeconds != null);
        Preconditions.checkState(damagePhaseSeconds != null);
        Preconditions.checkState(secondsForHalfHeart != null);
        Preconditions.checkState(phaseStartSender != null);
        Preconditions.checkState(updatesSender != null);
        Preconditions.checkState(damageSender != null);
        Preconditions.checkState(potentialAreas != null);
        Preconditions.checkState(notificationTicks != null);
        Preconditions.checkState(playerStrings != null);

        // add the areas onto the selector
        selection.setPotentialAreas(potentialAreas);

        return new MigrationRunnable(selection, phaseStartSender, updatesSender, damageSender, warningPhaseSeconds, damagePhaseSeconds, secondsForHalfHeart, notificationTicks, playerStrings);
    }
}
