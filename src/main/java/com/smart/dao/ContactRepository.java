package com.smart.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	// we will implement a method for pagenation here ...

	// user ka id leke contacts return karega

	// Ab yaha pe pagination implement karna h
	// We need two things
	// Page number[n]
	// Contacts per page
	@Query("from Contact c where c.user.id=:userId")
	public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);// Pageable object will
																							// return contacts according
																							// to page number and
																							// respective contacts

//	@Query("select c from Contact c where c.cId =:cId")
//	public Contact getContactById(@Param("cId") int cId);
}
