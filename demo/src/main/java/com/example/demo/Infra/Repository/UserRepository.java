    package com.example.demo.Infra.Repository;
    import java.util.Optional;
    import com.example.demo.Infra.Entities.UserEntity;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.jpa.repository.JpaRepository;
    import java.util.Optional;

    import org.springframework.data.jpa.repository.JpaRepository;

    import java.util.Optional;

    public interface UserRepository extends JpaRepository<UserEntity, Integer> {

        UserEntity findByEmail(String email);

        UserEntity findByUsername(String username);


    }