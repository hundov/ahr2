package mixin;

import net.minecraft.world.food.Foods;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Foods.class)
public class FoodsMixin {

    // Nutrition

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;nutrition(I)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 3
            )
    )
    private static int modifyBeetrootNutrition(int nutrition) {
        return 2;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;nutrition(I)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 4
            )
    )
    private static int modifyBreadNutrition(int nutrition) {
        return 4;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;nutrition(I)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 9
            )
    )
    private static int modifyCookedBeefNutrition(int nutrition) {
        return 7;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;nutrition(I)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 10
            )
    )
    private static int modifyCookedChickenNutrition(int nutrition) {
        return 7;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;nutrition(I)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 11
            )
    )
    private static int modifyCookedCodNutrition(int nutrition) {
        return 7;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;nutrition(I)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 12
            )
    )
    private static int modifyCookedMuttonNutrition(int nutrition) {
        return 7;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;nutrition(I)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 13
            )
    )
    private static int modifyCookedPorkchopNutrition(int nutrition) {
        return 7;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;nutrition(I)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 14
            )
    )
    private static int modifyCookedRabbitNutrition(int nutrition) {
        return 7;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;nutrition(I)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 15
            )
    )
    private static int modifyCookedSalmonNutrition(int nutrition) {
        return 7;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;nutrition(I)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 16
            )
    )
    private static int modifyCookieNutrition(int nutrition) {
        return 3;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;nutrition(I)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 18
            )
    )
    private static int modifyEnchantedGoldenAppleNutrition(int nutrition) {
        return 8;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;nutrition(I)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 19
            )
    )
    private static int modifyGoldenAppleNutrition(int nutrition) {
        return 6;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;nutrition(I)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 22
            )
    )
    private static int modifyMelonSliceNutrition(int nutrition) {
        return 3;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;nutrition(I)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 26
            )
    )
    private static int modifyPotatoNutrition(int nutrition) {
        return 2;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;nutrition(I)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 28
            )
    )
    private static int modifyPumpkinPieNutrition(int nutrition) {
        return 12;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;nutrition(I)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 33
            )
    )
    private static int modifySweetBerriesNutrition(int nutrition) {
        return 3;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;nutrition(I)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 34
            )
    )
    private static int modifyGlowBerriesNutrition(int nutrition) {
        return 3;
    }

    // Stews

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/Foods;stew(I)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 0
            )
    )
    private static int modifyBeetrootSoupNutrition(int nutrition) {
        return 8;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/Foods;stew(I)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 1
            )
    )
    private static int modifyMushroomStewNutrition(int nutrition) {
        return 8;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/Foods;stew(I)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 2
            )
    )
    private static int modifyRabbitStewNutrition(int nutrition) {
        return 12;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/Foods;stew(I)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 3
            )
    )
    private static int modifySuspiciousStewNutrition(int nutrition) {
        return 12;
    }

    // Saturation

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;saturationModifier(F)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 3
            )
    )
    private static float modifyBeetrootSaturation(float modifier) {
        return 1.0F;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;saturationModifier(F)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 4
            )
    )
    private static float modifyBreadSaturation(float modifier) {
        return 1.0F;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;saturationModifier(F)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 9
            )
    )
    private static float modifyCookedBeefSaturation(float modifier) {
        return 1.0F;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;saturationModifier(F)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 10
            )
    )
    private static float modifyCookedChickenSaturation(float modifier) {
        return 1.0F;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;saturationModifier(F)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 11
            )
    )
    private static float modifyCookedCodSaturation(float modifier) {
        return 1.0F;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;saturationModifier(F)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 12
            )
    )
    private static float modifyCookedMuttonSaturation(float modifier) {
        return 1.0F;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;saturationModifier(F)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 13
            )
    )
    private static float modifyCookedPorkchopSaturation(float modifier) {
        return 1.0F;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;saturationModifier(F)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 14
            )
    )
    private static float modifyCookedRabbitSaturation(float modifier) {
        return 1.0F;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;saturationModifier(F)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 15
            )
    )
    private static float modifyCookedSalmonSaturation(float modifier) {
        return 1.0F;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;saturationModifier(F)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 16
            )
    )
    private static float modifyCookieSaturation(float modifier) {
        return 0.5F;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;saturationModifier(F)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 18
            )
    )
    private static float modifyEnchantedGoldenAppleSaturation(float modifier) {
        return 1.0F;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;saturationModifier(F)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 19
            )
    )
    private static float modifyGoldenAppleSaturation(float modifier) {
        return 1.0F;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;saturationModifier(F)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 20
            )
    )
    private static float modifyGoldenCarrotSaturation(float modifier) {
        return 1.0F;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;saturationModifier(F)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 21
            )
    )
    private static float modifyHoneyBottleSaturation(float modifier) {
        return 0.5F;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;saturationModifier(F)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 22
            )
    )
    private static float modifyMelonSliceSaturation(float modifier) {
        return 0.5F;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;saturationModifier(F)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 28
            )
    )
    private static float modifyPumpkinPieSaturation(float modifier) {
        return 0.8F;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;saturationModifier(F)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 33
            )
    )
    private static float modifySweetBerriesSaturation(float modifier) {
        return 0.3F;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodProperties$Builder;saturationModifier(F)Lnet/minecraft/world/food/FoodProperties$Builder;",
                    ordinal = 34
            )
    )
    private static float modifyGlowBerriesSaturation(float modifier) {
        return 0.3F;
    }
}