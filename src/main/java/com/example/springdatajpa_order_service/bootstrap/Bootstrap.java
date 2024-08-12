package com.example.springdatajpa_order_service.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.springdatajpa_order_service.domain.Customer;
import com.example.springdatajpa_order_service.repos.CustomerRepo;
import com.example.springdatajpa_order_service.services.BootstrapOrderHeaderService;

@Component
public class Bootstrap implements CommandLineRunner{

    @Autowired
    BootstrapOrderHeaderService service;

    @Autowired
    CustomerRepo customerRepo;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("---------------------------------");

        // this is an external method call using proxy mode
        service.readOrderData();

        /*
         * Transactional Proxy Mode
         *      Here, run() is a external method but readOrderData() is
         *      a internal method. We need a proxy to call internal method
         *      inside the external method.
         */

        // checking optimistic locking
        var customer = new Customer();
        customer.setCustomerName("Testing Version");
        var savedCustomer = customerRepo.save(customer);
        System.out.println("Version id: " + savedCustomer.getVersion());

        // optimistic locking demo
        savedCustomer.setCustomerName("Testing version 2");
        var savedCustomer2 = customerRepo.save(savedCustomer);
        System.out.println("Version id: " + savedCustomer2.getVersion());

        savedCustomer2.setCustomerName("Testing version 3");
        var savedCustomer3 = customerRepo.save(savedCustomer2);
        System.out.println("Version id: " + savedCustomer3.getVersion());

        // this will cause error due to optimistic lock
        //customerRepo.delete(savedCustomer);

        // this will work without any problems because of updated object reference
        customerRepo.deleteById(savedCustomer3.getId());
    }
    
}
