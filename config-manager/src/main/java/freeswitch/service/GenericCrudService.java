package freeswitch.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public abstract class GenericCrudService<T, Id> {
    private final JpaRepository<T, Id> jpaRepository;


    protected GenericCrudService(JpaRepository<T, Id> jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    public ResponseEntity<String> deleteEntity(Id id) {
        try{
            jpaRepository.deleteById(id);
            return new ResponseEntity<>("DELETED", HttpStatus.OK);
        }catch(Exception e){
            System.err.println(e.getMessage());
            return new ResponseEntity<>("FAILED", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<T> getEntity(Id id) {
        try{
            return jpaRepository.findById(id).map(entity -> new ResponseEntity<>(entity, HttpStatus.OK))
                   .orElseThrow(() -> new Exception("Not Found"));
        }catch(Exception e){
            System.err.println(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<T> createEntity(T entity) {
        try{
            return new ResponseEntity<>(jpaRepository.save(entity), HttpStatus.CREATED);
        }catch(Exception e){
            System.err.println(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<T>> getEntities(){
        try{
            return new ResponseEntity<>(jpaRepository.findAll(), HttpStatus.OK);
        }catch(Exception e){
            System.err.println(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
