/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MiddlewareGenetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
import org.cloudsimplus.utilizationmodels.UtilizationModelFull;
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

    private static final int CLOUDLETS = 100;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 10_000; // Milion Instructions (MI)
    
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
                
        int num_cloudlet = 50;
        int num_vm = 15;
        
        vmList = createVms(num_vm); // creating n vms
        System.out.println("CANTIDAD DE VMS: "+vmList.size());
        
        for (int i=0;i<vmList.size();i++){
            System.out.println(vmList.get(i).getTotalMipsCapacity());
        }
            
        
        cloudletList = createCloudlets(num_cloudlet); // creating 40 cloudlets
     
        
        //Poblacion inicial
        //Se crea un array con Poblaciones. Habran n=num_vm Poblaciones. 
        //Con n=10 habran 10 poblaciones. Cada indice de initialPopulation tiene un array de Individuos
        ArrayList<Poblacion> initialPopulation = new ArrayList<Poblacion>();
        
        if (num_vm>num_cloudlet || num_vm==num_cloudlet){
            for(int j=0;j<num_vm;j++)
            {

                    ArrayList<Individuo> firstChromosome = new ArrayList<Individuo>();

                    for(int i=0;i<num_cloudlet;i++)
                    {
              
                            int k = (int)Math.floor(Math.random() * (vmList.size() - 0 + 1) + 0);
                            ArrayList<Cloudlet> cloudletForInd=new ArrayList<Cloudlet>();
                            cloudletForInd.add(cloudletList.get(i));
                            Individuo geneObj = new Individuo(cloudletForInd,vmList.get(k));
                            firstChromosome.add(geneObj);
                    }
                    Poblacion chromosome = new Poblacion(firstChromosome);
                    initialPopulation.add(chromosome);
            }
        }
   
        else if (num_vm<num_cloudlet){
            for(int j=0;j<num_vm;j++)
            {
                ArrayList<Individuo> firstChromosome = new ArrayList<Individuo>();

                for(int i=0;i<num_vm;i++)
                {
                        
                        int k = (int)Math.floor(Math.random() * (vmList.size()-1 - 0 + 1) + 0);
                        System.out.println("K:  "+k);
                        ArrayList<Cloudlet> cloudletForInd=new ArrayList<Cloudlet>();
                        // cloudletList.remove(i) elimina el elemento y retorna cloudlet del indice
                        cloudletForInd.add(cloudletList.get(i));
                        Individuo geneObj = new Individuo(cloudletForInd,vmList.get(k));
                        firstChromosome.add(geneObj);
                }
                for (int x=num_vm;x<num_cloudlet;x++){
                        int k = (int)Math.floor(Math.random() * (vmList.size()-1 - 0 + 1) + 0);
                        System.out.println("K:  "+k);
                        Cloudlet c=cloudletList.get(x);
                        //System.out.println("K: "+k);
                        firstChromosome.get(k).addCloudletForIndividuo(c);
                }
                
                
                Poblacion chromosome = new Poblacion(firstChromosome);
                initialPopulation.add(chromosome);
            }
        }
        
        for (int i=0; i<initialPopulation.size();i++){
            //System.out.println("Poulation: "+initialPopulation.get(i).getIndividuosList().get(i).getCloudletFromIndividuo().getLength());
            System.out.println("Poulation: "+initialPopulation.get(i).getIndividuosList().size());
        }


        int generation=40;
        

        
        int populationSize=initialPopulation.size();
        Random random = new Random();
        // Running the algorithm for 20 generations
        for(int count=0;count<=generation;count++){
                        
                        //se toman dos poblaciones de manera random
			int index1,index2;
			index1=random.nextInt(populationSize) % populationSize;
			index2=random.nextInt(populationSize) % populationSize;
			ArrayList<Individuo> l1= new ArrayList<Individuo>();
                        System.out.println("Index1: "+index1);
                        System.out.println("Index2: "+index2);
			l1=initialPopulation.get(index1).getIndividuosList();
			Poblacion cromosoma1 = new Poblacion(l1);
			ArrayList<Individuo> l2= new ArrayList<Individuo>();
			l2=initialPopulation.get(index2).getIndividuosList();
			Poblacion cromosoma2 = new Poblacion(l2);
			double rangeMin = 0.0f;

                        double rangeMax = 1.0f;
                        Random r = new Random();
                        double crossProb = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
                        //en caso de que la probabilidad calculada de manera random sea menor a 0.5 se realiza crossover de invdividuos random
                        if(crossProb<0.7)
                        {
                                int ind=0 + (int)(Math.random() * ((num_vm-1 - 0) + 1));
                                
                                //el crossover se realiza intercambiando las Vms de un index random
                                
                                Vm vm1 = l1.get(ind).getVmFromaIndividuo();
                                Vm vm2 = l2.get(ind).getVmFromaIndividuo();
                                cromosoma1.updateIndividuo(ind, vm2);
                                cromosoma2.updateIndividuo(ind, vm1);
                                initialPopulation.set(index1, cromosoma1);
                                initialPopulation.set(index2, cromosoma2);
                                
                        }
                        
            
        }
        
        int fittestIndex=0;
        double time=1000000;
        
        //se itera sobre las poblaciones buscando la poblacion con mejor fitteest, que se evalua sumando las divisiones entre cloudlet.length y vm.Mips
        for(int i=0;i<initialPopulation.size();i++)
        {
                ArrayList<Individuo> l= new ArrayList<Individuo>();
                l=initialPopulation.get(i).getIndividuosList();
                double sum=0;
                for(int j=0;j<num_vm;j++)
                {
                        Individuo g = l.get(j);
                        ArrayList<Cloudlet> cloudletsVm = g.getCloudletListFromIndividuo();
                        long sumLengthCloudlet=0;
                        for (int x=0;x<cloudletsVm.size();x++){
                            sumLengthCloudlet=sumLengthCloudlet+cloudletsVm.get(x).getLength();
                        }
                       
                        Vm v = g.getVmFromaIndividuo();
                        double temp = sumLengthCloudlet/v.getMips();
                        sum+=temp;
                        
                }
                System.out.println("Fittest value: "+sum);
                System.out.println("Index: "+i);
                if(sum<time)
                {
                        time=sum;
                        fittestIndex=i;
                }
        }
        System.out.println("Fittest: "+fittestIndex);
        
        
        ArrayList<Individuo> result = new ArrayList<Individuo>();
        result = initialPopulation.get(fittestIndex).getIndividuosList();
        /*
        for (int i=0;i<initialPopulation.size();i++){
            for (int j=0;j<initialPopulation.get(i).getIndividuosList().size();j++){
                System.out.println("Initial: "+initialPopulation.get(i).getIndividuosList().get(j).getCloudletFromIndividuo().getLength());
            }
            System.out.println(".............");
            
        }
        */
        System.out.println("Size de result: "+result.size());
        System.out.println(initialPopulation);
        System.out.println(result);

        List<Cloudlet> finalcloudletList = new ArrayList<Cloudlet>();
        List<Vm> finalvmlist = new ArrayList<Vm>();



        //se toman las cloudlet y vms. Se asigna cada Cloudlet a su respectiva Vm
        for(int i=0;i<result.size();i++)
        {
            
                Vm vm=result.get(i).getVmFromaIndividuo();
                finalvmlist.add(vm);
                
                System.out.println("Cloudlets lists length: "+result.get(i).getCloudletListFromIndividuo().size());
                for (int x=0; x<result.get(i).getCloudletListFromIndividuo().size();x++){
                    System.out.println("Vm index: "+i);
                    Cloudlet cloudlet=result.get(i).getCloudletListFromIndividuo().get(x);
                    cloudlet.setVm(vm);
                    finalcloudletList.add(cloudlet); 
                }

                

        }

        for (int i=0;i<finalvmlist.size();i++){
            System.out.println(finalvmlist.get(i).getTotalMipsCapacity());
        }
        //se asignan las vm y cloudlet al broker
        System.out.println("CANTIDAD DE VMS FINALES: "+finalvmlist.size());
        broker0.submitVmList(finalvmlist);
        broker0.submitCloudletList(finalcloudletList);

        // Starts the simulation
        simulation.start();

        // Print results when simulation is over


        final var cloudletFinishedList = broker0.getCloudletFinishedList();
        new CloudletsTableBuilder(cloudletFinishedList).build();


 
       
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
        /*
        for (int i = 0; i < num_vm; i++) {
            Random rand = new Random();
            int x = rand.nextInt(3);
            switch (x) {
                case 0:
                    vmList.add(createVmBasic());
                    break;
                case 1:
                    vmList.add(createVmMedium());
                    break;
                case 2:
                    vmList.add(createVmAdvanced());
                    break;
                default:
                    break;
            }
            
        }
        */
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
