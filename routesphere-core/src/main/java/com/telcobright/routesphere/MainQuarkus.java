package com.telcobright.routesphere;

import com.telcobright.routesphere.utils.Starter;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.enterprise.context.ApplicationScoped;

@QuarkusMain
@ApplicationScoped
public class MainQuarkus {
    public static void main(String[] args) {
        Quarkus.run(Starter.class, args);
    }
}
