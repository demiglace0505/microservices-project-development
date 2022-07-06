package com.demiglace.location.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.demiglace.location.entities.Location;

public interface LocationRepository extends JpaRepository<Location, Integer> {
	@Query(value="SELECT type, COUNT(*) FROM vendor GROUP BY type", nativeQuery=true)
	public List<Object[]> findTypeAndTypeCount();
}
