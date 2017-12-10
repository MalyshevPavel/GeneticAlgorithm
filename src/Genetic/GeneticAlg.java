package Genetic;

import java.util.*;

public class GeneticAlg {
    private Random random = new Random();
    private double mutateProb;
    private int genNum;
    private int firstGenAmount;
    private int bestBreed;
    private int maxWeight;
    private List<Item> items;
    private List<Chromosome> chromosomes;
    private Chromosome best;

    public GeneticAlg(double mutateProb, int genNum, int firstGenAmount, int bestBreed) {
        this.mutateProb = mutateProb;
        this.genNum = genNum;
        this.firstGenAmount = firstGenAmount;
        this.bestBreed = bestBreed;
    }


    public Fill fillKnapsackGenetic(int maxWeight, List<Item> items) {
        this.items = items;
        this.maxWeight = maxWeight;
        return fillKnapsackGenetic().generateFill();

    }

    private Chromosome fillKnapsackGenetic() {
        chromosomes = generateChromosomes(firstGenAmount);
        for (int i = 0; i < genNum; i++) {
            List<Chromosome> crossBreed = generateCrossBreed();
            chromosomes = mutate(crossBreed);
        }
        return best;
    }

    private List<Chromosome> generateChromosomes(int number) {
        List<Chromosome> result = new ArrayList<Chromosome>();
        for (int i = 0; i < number; i++) {
            result.add(new Chromosome());
        }
        best = result.get(0);
        return result;
    }

    private List<Chromosome> mutate(List<Chromosome> generation) {
        for (Chromosome chromosome : generation) {
            if (random.nextDouble() < mutateProb) {
                chromosome = chromosome.mutate();
            }
        }
        return generation;
    }


    private class Chromosome {
        byte[] gens;
        int fitness;
        int load;

        Chromosome() {
            load = 0;
            fitness = 0;
            gens = new byte[items.size()];
            int startPosition = random.nextInt(gens.length);
            int toLeft = startPosition;
            int toRight = startPosition++;
            while ((toLeft != 0) || (toRight != gens.length - 1)) {
                if (toLeft != 0) {
                    if (random.nextInt(gens.length) == toLeft) {
                        gens[toLeft] = 1;
                        load += items.get(toLeft).getWeight();
                        fitness += items.get(toLeft).getCost();
                    }
                    toLeft--;
                }
                if (toRight != gens.length - 1) {
                    if (random.nextInt(gens.length) == toRight) {
                        gens[toRight] = 1;
                        load += items.get(toRight).getWeight();
                        fitness += items.get(toRight).getCost();
                    }
                    toRight++;
                }
            }

            if (load > maxWeight) {
                fitness = -100;
            }
        }

        Chromosome(byte[] gens, int load, int fitness) {
            this.gens = gens;
            this.load = load;
            this.fitness = fitness;
        }

        Chromosome mutate() {
            byte[] gensMutant = new byte[items.size()];
            int fitness = 0;
            int load = 0;
            for (int i = 0; i < items.size(); i++) {
                gensMutant[i] = random.nextInt(2) == 1 ? gens[i] : (byte) random.nextInt(2);
                if (gensMutant[i] == 1) {
                    load += items.get(i).getWeight();
                    fitness += items.get(i).getCost();
                }
            }

            if (load > maxWeight) {
                fitness = -100;
            }
            return new Chromosome(gensMutant, load, fitness);
        }


        private Chromosome crossBreed(Chromosome other) {
            byte[] gensOfChild = new byte[gens.length];
            int load = 0;
            int fitness = 0;
            for (int i = 0; i < gens.length; i++)
                if (gens[i] == other.gens[i]) {
                    gensOfChild[i] = gens[i];
                    if (gens[i] == 1) {
                        load += items.get(i).getWeight();
                        fitness += items.get(i).getCost();
                    }
                } else {
                    gensOfChild[i] = (byte) random.nextInt(2);
                    if (gensOfChild[i] == 1) {
                        load += items.get(i).getWeight();
                        fitness += items.get(i).getCost();
                    }
                }

            if (load > maxWeight) {
                fitness = -100;
            }

            return new Chromosome(gensOfChild, load, fitness);
        }

        Fill generateFill() {
            Set<Item> takenItems = new HashSet<>();
            for (int i = 0; i < items.size(); i++)
                if (gens[i] == 1)
                    takenItems.add(items.get(i));
            return new Fill(fitness, takenItems);
        }
    }

    private List<Chromosome> generateCrossBreed() {
        List<Chromosome> result = new ArrayList<>();

        chromosomes.sort((Chromosome o1, Chromosome o2) -> Integer.compare(o2.fitness, o1.fitness));
        chromosomes = chromosomes.subList(0, bestBreed);

        if (chromosomes.get(0).fitness > best.fitness) {
            best = chromosomes.get(0);
        }


        for (int i = 0; i < firstGenAmount; i++) {
            Chromosome first = chromosomes.get(random.nextInt(bestBreed));
            Chromosome second = chromosomes.get(random.nextInt(bestBreed));
            result.add(first.crossBreed(second));
        }

        result.sort((Chromosome o1, Chromosome o2) -> Integer.compare(o2.fitness, o1.fitness));

        return result;
    }
}