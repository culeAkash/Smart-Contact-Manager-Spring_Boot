package com.smart.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	// we will implement a method for pagenation here ...

	// user ka id leke contacts return karega

	@Query("from Contact c where c.user.id=:userId")
	public List<Contact> findContactsByUser(@Param("userId") int userId);
}
