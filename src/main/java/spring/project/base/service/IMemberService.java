package spring.project.base.service;

import spring.project.base.entity.Member;

import java.util.List;

public interface IMemberService {

    List<Member> findAll();

    Member findById(int id);


    void deleteById(int id);
}