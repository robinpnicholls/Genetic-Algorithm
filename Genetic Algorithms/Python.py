gene_size = 8
rule_size = 4
from gene import Gene
from rules import Rule
from individual import Individual
from read_file import Read_File
import re
import copy
import random
import read_file
from random import randint
pop = 5
gen = 1000
cross_over_rate = 60
mutation_rate = 7
shuffle_chance = 10

data_points = 2000
training_testing_split = 50
training_set = []
testing_set = []


def main():
    #print("start")
    read_from_file()
    population = [Individual() for i in range(pop)]

    # for i in population:
    #     for rule in i.rules:
    #         for gene in rule.condition:
    #             #if gene.is_hash:
    #             #    print("#", end="", flush=True)
    #             #else:
    #             print("(", gene.lower_bound, ",", gene.lower_bound, ")", end="", flush=True)
    #         print("", rule.action, " ", end="", flush=True)
    #     print("")

    print("Initial Population")
    evaluate_fitness_training(population)
    best_fitness(population)
    mean_fitness(population)

    print("")
    for i in range(gen):
        evolve(population, i)
    get_best_individual(population)



def evolve(population, iteration):
    fitness = 0
    p = 0
    for k in range(pop):
        if population[k].fitness > fitness:
            p = k
            fitness = population[k].fitness

    best_individual = copy.deepcopy(population[p])

    selection(population)
    cross_over(population)
    if (iteration < 3500):
        mutation(population)
    else:
        mutation_with_step(population)

    shuffle(population)
    print((iteration + 1), ": ", end="", flush=True)
    #evaluateFitness(population)
    for k in range(pop):
        if population[k].fitness < fitness:
            p = k
            fitness = population[k].fitness

    population[p] = copy.deepcopy(best_individual)
    evaluate_fitness_training(population)
    best_fitness(population)
    mean_fitness(population)

    evaluate_fitness_testing(population)

    # for i in population:
    #     for rule in i.rules:
    #         for gene in rule.condition:
    #             #if gene.is_hash:
    #             #    print("#", end="", flush=True)
    #             #else:
    #             print("(", gene.lower_bound, ",", gene.upper_bound, ")", end="", flush=True)
    #         print("", rule.action, " ", end="", flush=True)
    #     print("")

    return population


def selection(population):
    offspring = []

    for i in range(pop):
        parent1 = randint(0, (pop - 1))
        parent2 = randint(0, (pop - 1))
        if population[parent1].fitness >= population[parent2].fitness:
            offspring.append(copy.deepcopy(population[parent1]))
        else:
            offspring.append(copy.deepcopy(population[parent2]))

    for i in range(pop):
        population[i] = copy.deepcopy(offspring[i])

    return population


def cross_over(population):

    for i in range(pop):
        if randint(0, 100) < cross_over_rate:
            cross_over_point = randint(1, rule_size - 1)
            temp_rule = []
            for k in range(cross_over_point):
                #print(k)
                temp_rule.append(copy.deepcopy(population[i].rules[k]))
            for k in range(cross_over_point):
                population[i].rules[k] = copy.deepcopy(population[i + 1].rules[k])
            for k in range(cross_over_point):
                population[i + 1].rules[k] = copy.deepcopy(temp_rule[k])
        if i == (pop - 2):
            break
    return population


def mutation(population):
    for i in population:
        for j in range(rule_size):
            for k in range(gene_size - 1):
                if random.uniform(0, 100) < mutation_rate:
                    if random.uniform(0, 1) < 0.33:
                        i.rules[j].condition[k].is_hash ^= True
                    upper_bound = randint(i.rules[j].condition[k].lower_bound, 99)
                    lower_bound = randint(1, i.rules[j].condition[k].upper_bound)
                    #print("upper:", i.rules[j].condition[k].upper_bound)
                    #print("lower:", i.rules[j].condition[k].lower_bound)

                    i.rules[j].condition[k].upper_bound = upper_bound
                    i.rules[j].condition[k].lower_bound = lower_bound

                    #print("upper:", i.rules[j].condition[k].upper_bound)
                    #print("lower:", i.rules[j].condition[k].lower_bound)

            if random.uniform(0, 100) < mutation_rate:
                i.rules[j].action = randint(1, 4)

    return population


def mutation_with_step(population):
    for i in population:
        for j in rule_size:
            for k in (gene_size - 1):
                if random.uniform(0,100) < mutation_rate:
                    upper_bound = randint((i.rules[j].condition[k].upper_bound - 5), (i.rules[j].condition[k].upper_bound + 5))
                    lower_bound = randint((i.rules[j].condition[k].lower_bound - 5), (i.rules[j].condition[k].lower_bound + 5))

                    if (upper_bound > 99):
                        upper_bound = 99
                    if (lower_bound < 1):
                        lower_bound = 1
                    if (lower_bound > 99):
                        lower_bound = 99
                    if (upper_bound < 1):
                        upper_bound = 1

                    i.rules[j].condition[k].upper_bound = upper_bound
                    i.rules[j].condition[k].lower_bound = lower_bound

            if random.uniform(0, 100 < mutation_rate):
                i.rules[j].action = randint(1, 4)
    return population



def evaluate_fitness_training(population):
    #print("e for training fitness ", end="", flush=True)
    for i in population:
        #print("i.fitness =", i.fitness, " ", end="", flush=True)
        i.fitness = 0
        i.fitness = compare_to_file(i.rules, training_set)
    return population

def evaluate_fitness_testing(population):
    #print("e for testing fitness ", end="", flush=True)
    #print("point")
    all_fitness = []
    best_fitness = 0
    mean_fitness = 0
    p = 0

    for i in population:
        fitness = compare_to_file(i.rules, testing_set)
        #print(fitness)
        all_fitness.append(fitness)

        if best_fitness < fitness:
            best_fitness = fitness
            p = i

        mean_fitness = mean_fitness + fitness
    #print(p)
    #print(population[p])
    #best_fitness = compare_to_file(population[p].rules, testing_set)
    mean_fitness = mean_fitness / pop
    print(best_fitness, ",", mean_fitness)

    #return population

def best_fitness(population):
    best_fitness = 0
    for i in population:
        if i.fitness > best_fitness:
            best_fitness = i.fitness
    print(best_fitness, ",", end="", flush=True)
    return population

def mean_fitness(population):
    mean_fitness = 0
    for i in population:
        mean_fitness += i.fitness
    mean_fitness = mean_fitness / pop
    print(mean_fitness, ",", end="", flush=True)
    return population




def compare_to_file(rules, file):
    fitness = 0
    for j in range(999):
        #print(j)
        #print("j:", end="", flush=True)
        #print(j)
        #match = True
        for rule in rules:
            #for gene in rule.condition:
                #print("(", gene.lower_bound, ",", gene.upper_bound, ")", end="", flush=True)
            match = True
            for i in range(gene_size - 1):
                #print("i:", end="", flush=True)
                #print(j)
                #print(file[j].line[0])
                if rule.condition[i].is_hash:
                    print("", end="", flush=True)
                    #match, move on
                elif (file[j].line[i] < rule.condition[i].upper_bound) and (file[j].line[i] > rule.condition[i].lower_bound):
                    print("", end="", flush=True)
                    #match, move on
                else:
                    match = False
                    #no match
                    break
            #print(match)
            #print(rule.action)
            #print(file[j].line[gene_size - 1])
            if rule.action == file[j].line[gene_size - 1] and match is True:
                fitness = fitness + 1
                break
            elif match:
                break
    return fitness




def get_best_individual(population):
    best_fitness = 0
    pointer = 0
    for i in range(pop):
        if population[i].fitness > best_fitness:
            best_fitness = population[i].fitness
            pointer = i
    print("best individual with fitness:", population[pointer].fitness, " | ", end="", flush=True)
    for rule in population[pointer].rules:
        for gene in rule.condition:
            #if gene.is_hash:
            #    print(" #, ", end="", flush=True)
            #else:
            print(" (", gene.lower_bound, ":", gene.upper_bound, "), ", end="", flush=True)
        print("", rule.action, end="", flush=True)
    return population


def shuffle(population):
    temp_rules = []
    for individual in population:
        if randint(0, 100) < shuffle_chance:
            position = randint(1, rule_size)
            #print("position:", position)
            k = 0
            for i in range(position, rule_size):
                #print("i:", i, " k:", k)
                temp_rules.append(copy.deepcopy(individual.rules[i]))
                k = k + 1
            for i in range(0, position):
                #print("i:", i, " k:", k)
                temp_rules.append(copy.deepcopy(individual.rules[i]))
                k = k + 1
            for i in range(0, rule_size):
                #print("i:", i, " k:", k)
                individual.rules[i] = copy.deepcopy(temp_rules[i])
    return population


def read_from_file():
    regex = re.compile(r'[\n\r\t]')
    with open("UCI_1") as f:
        content = f.readlines()
    content = [x.strip() for x in content]

    for i, line in enumerate(content):
        line = regex.sub(" ", line)
        if i < 1001:
            Individual()
            #population = [Individual() for i in range(pop)]
            testing_set.append(Read_File([int(s) for s in line.split(" ")]))
        else:
            training_set.append(Read_File([int(s) for s in line.split(" ")]))

    #for obj in testing_set:
        #print(obj.line)




def test():
    print("test")


main()


