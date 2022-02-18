package com.epam.esm.repository;

import com.epam.esm.repository.model.Tag;

import java.util.List;

/*
* A specification how to interact with datasource which contains tags
* @author Flexus
* */
public interface TagRepository extends GenericRepository<Tag>,Identifiable<Tag>,Nameable<Tag>{
    List<Tag> fetchAssociatedTags(long certificateID);
}
