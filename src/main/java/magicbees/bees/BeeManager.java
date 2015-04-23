package magicbees.bees;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import magicbees.block.types.HiveType;
import magicbees.item.types.DropType;
import magicbees.main.Config;
import magicbees.main.utils.Tuple;
import magicbees.main.utils.compat.ExtraBeesHelper;
import net.minecraft.item.ItemStack;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeRoot;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;

public class BeeManager
{
	public static IBeeRoot beeRoot;

	private static List<Tuple<IAlleleBeeSpecies, Double>> worldgenSpeciesWeights = new ArrayList<Tuple<IAlleleBeeSpecies, Double>>();
	private static double worldgenSpeciesWeightsTotal = 0;
	
	public static void ititializeBees()
	{
		beeRoot = (IBeeRoot)AlleleManager.alleleRegistry.getSpeciesRoot("rootBees");
		
		Allele.setupAdditionalAlleles();
		BeeSpecies.setupBeeSpecies();
		Allele.registerDeprecatedAlleleReplacements();
		BeeMutation.setupMutations();
		
		HiveType.initHiveData();
		
		beeRoot.setResearchSuitability(Config.drops.getStackForType(DropType.INTELLECT), 0.5f);
	}
	
	public static ItemStack getDefaultItemStackForSpecies(IAlleleBeeSpecies species, EnumBeeType type) {
		IBee bee = getBeeFromSpecies(species, false);
		return BeeManager.beeRoot.getMemberStack(bee, type.ordinal());
	}
	
	public static IBee getBeeFromSpecies(IAlleleBeeSpecies species, boolean applyRainResist) {
		IAllele[] speciesTemplate = BeeManager.beeRoot.getTemplate(species.getUID());
		
		if (applyRainResist) {
			speciesTemplate = BeeGenomeManager.addRainResist(speciesTemplate);
		}
		
		IBeeGenome genome = BeeManager.beeRoot.templateAsGenome(speciesTemplate);
		return BeeManager.beeRoot.getBee(null, genome);
	}
	
	public static IAlleleBeeSpecies getRandomWorldgenSpecies(Random r) {
		Collections.shuffle(worldgenSpeciesWeights);
		double value = r.nextDouble() * worldgenSpeciesWeightsTotal;
		IAlleleBeeSpecies species = Allele.getBaseSpecies("Forest");
		
		for (Tuple<IAlleleBeeSpecies, Double> t : worldgenSpeciesWeights) {
			value -= t.right.doubleValue();
			if (value <= 0.0) {
				species = t.left;
				break;
			}
		}
		
		return species;
	}
	
	public static void addWorldgenSpeciesWeight(IAlleleBeeSpecies species, double d) {
		worldgenSpeciesWeights.add(new Tuple<IAlleleBeeSpecies, Double>(species, d));
		worldgenSpeciesWeightsTotal += d;
	}
	
	public static void populateSpeciesListRarity() {
		worldgenSpeciesWeights.add(new Tuple<IAlleleBeeSpecies, Double>(Allele.getBaseSpecies("Forest"), 20.0));
		worldgenSpeciesWeights.add(new Tuple<IAlleleBeeSpecies, Double>(Allele.getBaseSpecies("Meadows"), 20.0));
		worldgenSpeciesWeights.add(new Tuple<IAlleleBeeSpecies, Double>(Allele.getBaseSpecies("Tropical"), 10.0));
		worldgenSpeciesWeights.add(new Tuple<IAlleleBeeSpecies, Double>(Allele.getBaseSpecies("Modest"), 16.0));
		worldgenSpeciesWeights.add(new Tuple<IAlleleBeeSpecies, Double>(Allele.getBaseSpecies("Wintry"), 10.0));
		worldgenSpeciesWeights.add(new Tuple<IAlleleBeeSpecies, Double>(Allele.getBaseSpecies("Ended"), 0.5));
		
		if (ExtraBeesHelper.isActive()) {
			worldgenSpeciesWeights.add(new Tuple<IAlleleBeeSpecies, Double>(Allele.getExtraSpecies("Water"), 12.0));
			worldgenSpeciesWeights.add(new Tuple<IAlleleBeeSpecies, Double>(Allele.getExtraSpecies("Rock"), 16.0));
			worldgenSpeciesWeights.add(new Tuple<IAlleleBeeSpecies, Double>(Allele.getExtraSpecies("Basalt"), 10.0));
		}

		worldgenSpeciesWeights.add(new Tuple<IAlleleBeeSpecies, Double>(BeeSpecies.MYSTICAL, 20.0));
		worldgenSpeciesWeights.add(new Tuple<IAlleleBeeSpecies, Double>(BeeSpecies.UNUSUAL, 20.0));
		worldgenSpeciesWeights.add(new Tuple<IAlleleBeeSpecies, Double>(BeeSpecies.SORCEROUS, 13.0));
		worldgenSpeciesWeights.add(new Tuple<IAlleleBeeSpecies, Double>(BeeSpecies.ATTUNED, 6.0));
		worldgenSpeciesWeights.add(new Tuple<IAlleleBeeSpecies, Double>(BeeSpecies.INFERNAL, 10.0));
		worldgenSpeciesWeights.add(new Tuple<IAlleleBeeSpecies, Double>(BeeSpecies.OBLIVION, 1.0));
		
		for (Tuple<IAlleleBeeSpecies, Double> t : worldgenSpeciesWeights) {
			worldgenSpeciesWeightsTotal += t.right.doubleValue();
		}
	}
}
