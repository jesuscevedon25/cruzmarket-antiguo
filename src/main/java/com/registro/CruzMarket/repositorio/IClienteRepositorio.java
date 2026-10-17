package com.registro.CruzMarket.repositorio;

import com.registro.CruzMarket.modelo.Cliente;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IClienteRepositorio extends JpaRepository<Cliente, Long> {

	Optional<Cliente> findByEmail(String email);
}