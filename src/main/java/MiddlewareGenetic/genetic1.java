/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MiddlewareGenetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Comparator;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.utilizationmodels.UtilizationModelDynamic;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;


/**
 *
 * @author FD_gi
 */
public class genetic1 {
    
    private static final int VM_PES = 4;
    
    private static final int  HOSTS = 100;
    private static final int  HOST_PES = 32;
    private static final int  HOST_MIPS = 10000; // Milion Instructions per Second (MIPS)
    private static final int  HOST_RAM = 65_536; //in Megabytes
    private static final long HOST_BW = 10_000; //in Megabits/s
    private static final long HOST_STORAGE = 1_000_000; //in Megabytes
    
    private final CloudSimPlus simulation;
    private final DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    
    public static void main(String[] args) {
        new genetic1();
    }
    
    public genetic1() {
        simulation = new CloudSimPlus();
        datacenter0 = createDatacenter();
        broker0 = new DatacenterBrokerSimple(simulation);
                
        int num_cloudlet = 100;
        int num_vm = 20;
        int initialPopulationSize = 20;
        
        vmList = createVms(num_vm); // creating n vms
        System.out.println("CANTIDAD DE VMS: "+vmList.size());          
        
        cloudletList = createCloudlets(num_cloudlet); // creating 40 cloudlets
     
        
        //Poblacion inicial
        //Se crea un array con Poblaciones. Habran n=num_vm Poblaciones. 
        //Con n=10 habran 10 poblaciones. Cada indice de initialPopulation tiene un array de Individuos
        ArrayList<Individuo> population = new ArrayList<Individuo>();
        for (int i=0; i<initialPopulationSize; i++){
            ArrayList<Cromosoma> firstIndividuo = new ArrayList<Cromosoma>();
            
            for (int j=0; j<num_vm;j++){
                Cromosoma geneObj = new Cromosoma(vmList.get(j));
                firstIndividuo.add(geneObj);
            }
            
            for (int k=0; k<num_cloudlet; k++){
                int rndIndex = (int)(Math.random() * (vmList.size()-1));
                firstIndividuo.get(rndIndex).addCloudletForCromosoma(cloudletList.get(k));
            }
            
            Individuo individuo = new Individuo(firstIndividuo);
            population.add(individuo);
        }

        this.setFitness(population, num_vm);
        
        int generation=20;
        int populationSize=population.size();
        Random random = new Random();
        // Running the algorithm for 20 generations
        for(int count=0;count<=generation;count++){
            //for(int g =0;g < (population.size())/2;g=g+2){
            
                int index1,index2;
                index1=0;
                index2=1;
                ArrayList<Cromosoma> l1= new ArrayList<Cromosoma>();
                l1=population.get(index1).getCromosomasList();
                Individuo hijo1 = new Individuo(l1);
                ArrayList<Cromosoma> l2= new ArrayList<Cromosoma>();
                l2=population.get(index2).getCromosomasList();
                Individuo hijo2 = new Individuo(l2);
                double rangeMin = 0.0f;
                
                double rangeMax = 1.0f;
                Random r = new Random();
                
                //MUTACIÓN
                /*double mutationProb = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
                if(mutationProb <= 0.3)
                {
                    int ind1=(int)(Math.random() * (num_vm-1));
                    int ind2=(int)(Math.random() * (num_vm-1));
                    
                    int ind3=(int)(Math.random() * (num_vm-1));
                    int ind4=(int)(Math.random() * (num_vm-1));
                    
                    //Se intercambian las cloudlest lists de los cromosomas
                    Cromosoma cromo1 = l1.get(ind1);
                    Cromosoma cromo2 = l1.get(ind2);
                    
                    ArrayList<Cloudlet> cloudListAux = cromo1.getCloudletListFromCromosoma();
                    cromo1.setCloudletList(cromo2.getCloudletListFromCromosoma());
                    cromo2.setCloudletList(cloudListAux);
                
                    //Se devuelven a la lista
                    l1.set(ind1, cromo1);
                    l1.set(ind2, cromo2);
                    
                    
                    //Se intercambian las cloudlest lists de los cromosomas
                    Cromosoma cromo3 = l2.get(ind3);
                    Cromosoma cromo4 = l2.get(ind4);
                    
                    cloudListAux = cromo3.getCloudletListFromCromosoma();
                    cromo3.setCloudletList(cromo4.getCloudletListFromCromosoma());
                    cromo4.setCloudletList(cloudListAux);
                    
                    //Se devuelven a la lista
                    l2.set(ind3, cromo3);
                    l2.set(ind4, cromo4);
                    
                    //Se devuelven las listas a la poblacion para guardar los cambios en los padres
                    population.set(0,new Individuo(l1));
                    population.set(1,new Individuo(l2)); 
                
                }*/
            
                //CRUCE
                double crossProb = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
                //en caso de que la probabilidad calculada de manera random sea menor a 0.5 se realiza crossover de invdividuos random
                if(crossProb <= 0.7)
                {   
                    int separationIndex = (int)(num_cloudlet/2);
                    int vm_index1;
                    int vm_index2;
                    Individuo parent1 = population.get(index1);
                    Individuo parent2 = population.get(index1);
                    
                    ArrayList<Cromosoma> listHijo1 =  hijo1.getCromosomasList();
                    ArrayList<Cromosoma> listHijo2 =  hijo2.getCromosomasList();
                            
                    for (int j = 0; j < num_vm;j++){
                        hijo1.updateCromosoma(j, vmList.get(j));
                        hijo2.updateCromosoma(j, vmList.get(j));
                    }
                    
                    for(int i = 0; i < separationIndex; i++){
                        vm_index1 = parent1.findCloudlet(cloudletList.get(i));
                        vm_index2 = parent2.findCloudlet(cloudletList.get(i));
                        
                        listHijo1.get(vm_index1).addCloudletForCromosoma(cloudletList.get(i));
                        listHijo2.get(vm_index2).addCloudletForCromosoma(cloudletList.get(i));
                    }
                    
                    for(int k = separationIndex; k < num_cloudlet; k++){
                        vm_index1 = parent1.findCloudlet(cloudletList.get(k));
                        vm_index2 = parent2.findCloudlet(cloudletList.get(k));
                        
                        listHijo1.get(vm_index1).addCloudletForCromosoma(cloudletList.get(k));
                        listHijo2.get(vm_index2).addCloudletForCromosoma(cloudletList.get(k));
                    }
                    
                    hijo1 = new Individuo(listHijo1);
                    hijo2 = new Individuo(listHijo2);
                    
                    population.add(hijo1);
                    population.add(hijo2);                
                }
            //}
                
            this.setFitness(population, num_vm);
            population = new ArrayList<Individuo>(population.subList(0, 20));
        }
        
        //se itera sobre las poblaciones buscando la poblacion con mejor fitteest, que se evalua sumando las divisiones entre cloudlet.length y vm.Mips    
        
        ArrayList<Cromosoma> result = new ArrayList<Cromosoma>();
        result = population.get(0).getCromosomasList();

        List<Cloudlet> finalcloudletList = new ArrayList<Cloudlet>();
        List<Vm> finalvmlist = new ArrayList<Vm>();



        //se toman las cloudlet y vms. Se asigna cada Cloudlet a su respectiva Vm
        int o = 0;
        for(int i=0;i<result.size();i++)
        {            
            Vm vm=result.get(i).getVmFromCromosoma();
            finalvmlist.add(vm);
                
            for (int x=0; x<result.get(i).getCloudletListFromCromosoma().size();x++){
                Cloudlet cloudlet=result.get(i).getCloudletListFromCromosoma().get(x);
                cloudlet.setVm(vm);
                finalcloudletList.add(cloudlet); 
                o = o + 1;
            }
        }
        
        //se asignan las vm y cloudlet al broker
        broker0.submitVmList(finalvmlist);
        broker0.submitCloudletList(finalcloudletList);

        // Starts the simulation
        simulation.start();

        // Print results when simulation is over


        final var cloudletFinishedList = broker0.getCloudletFinishedList();
        new CloudletsTableBuilder(cloudletFinishedList).build();
        
        System.out.println(o);
    }
    
    private void setFitness(ArrayList<Individuo> population, int num_vm){
        
        int fittestIndex=0;
        
        for(int i=0;i<population.size();i++)
        {
                ArrayList<Cromosoma> l= new ArrayList<Cromosoma>();
                l=population.get(i).getCromosomasList();
                double sum=0;
                for(int j=0;j<num_vm;j++)
                {
                        Cromosoma g = l.get(j);
                        ArrayList<Cloudlet> cloudletsVm = g.getCloudletListFromCromosoma();
                        long sumLengthCloudlet=0;
                        for (int x=0;x<cloudletsVm.size();x++){
                            sumLengthCloudlet=sumLengthCloudlet+cloudletsVm.get(x).getLength();
                        }
                       
                        Vm v = g.getVmFromCromosoma();
                        double temp = sumLengthCloudlet/v.getMips();
                        sum+=temp;
                        
                }
                
                Individuo ind = population.get(i);
                ind.setFitness(sum);
        }
        
        population.sort(Comparator.comparing(Individuo::getFitness));
    }
    
    private Datacenter createDatacenter() {
        final var hostList = new ArrayList<Host>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            final var host = createHost();
            hostList.add(host);
        }

        //Uses a VmAllocationPolicySimple by default to allocate VMs
        return new DatacenterSimple(simulation, hostList);
    }

    private Host createHost() {
        final var peList = new ArrayList<Pe>(HOST_PES);
        //List of Host's CPUs (Processing Elements, PEs)
        for (int i = 0; i < HOST_PES; i++) {
            //Uses a PeProvisionerSimple by default to provision PEs for VMs
            peList.add(new PeSimple(HOST_MIPS));
        }

        /*
        Uses ResourceProvisionerSimple by default for RAM and BW provisioning
        and VmSchedulerSpaceShared for VM scheduling.
        */
        return new HostSimple(HOST_RAM, HOST_BW, HOST_STORAGE, peList);
    }
    
    private Vm createVmBasic(){
        final var vm = new VmSimple(1000, VM_PES);
        vm.setRam(512).setBw(1000).setSize(1000);
        return vm;
    }
    private Vm createVmMedium(){
        final var vm = new VmSimple(5000, VM_PES);
        vm.setRam(1024).setBw(1500).setSize(2000);
        return vm;
    }    
    private Vm createVmAdvanced(){
        final var vm = new VmSimple(10_000, VM_PES);
        vm.setRam(2048).setBw(2000).setSize(3000);
        return vm;
    }    
    

    //devuelve una lista con Vms de diferentes categorias
    //20% de numVm son basicas, 40% medias y 40% avanzadas
    private List<Vm> createVms(int num_vm) {
        final var vmList = new ArrayList<Vm>(num_vm);
        double porcentajeBasic=num_vm*0.20;
        int numBasic= (int) porcentajeBasic;
        double porcentajeMedium=num_vm*0.40;
        int numMedium= (int) porcentajeMedium;
        
        double porcentajeAdvanced=num_vm*0.40;
        int numAdvanced= (int) porcentajeAdvanced;
        
        for (int i=0;i<numBasic;i++){
            vmList.add(createVmBasic());
        }    
        for (int i=0; i<numMedium;i++){
            vmList.add(createVmMedium());
        }
        for (int i=0; i<numAdvanced;i++){
            
            vmList.add(createVmAdvanced());
        }

        return vmList;
    }
    private List<Cloudlet> createCloudlets(int num_cloudlet) {
        final var cloudletList = new ArrayList<Cloudlet>(num_cloudlet);
        long length = 400_000;
        int pesNumber = 1;
        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        //final var utilizationModel =  new UtilizationModelFull();
        final var utilizationModel =  new UtilizationModelDynamic(0.1);

        for (int i = 0; i < num_cloudlet; i++) {
            //int x = (int) (Math.random() * ((2000 - 1) + 1)) + 1;
            final var cloudlet = new CloudletSimple(length, pesNumber, utilizationModel);
            cloudlet.setSizes(1024);
            cloudlet.setFileSize(300);
            cloudlet.setOutputSize(300);
            cloudletList.add(cloudlet);
        }

        return cloudletList;
    }
}
