package player;

public final class AHRPlayerExperience {

    public static final double XP_MULTIPLIER = 0.25D;

    public static Result modify(int amount, double remainder) {
        if (amount <= 0) {
            return new Result(amount, remainder);
        }

        double scaledExperience = amount * XP_MULTIPLIER;
        double totalExperience = remainder + scaledExperience;

        int wholeExperience = (int) totalExperience;
        double newRemainder = totalExperience - wholeExperience;

        return new Result(wholeExperience, newRemainder);
    }

    public record Result(
            int experience,
            double remainder
    ) {
    }

    private AHRPlayerExperience() {
    }
}