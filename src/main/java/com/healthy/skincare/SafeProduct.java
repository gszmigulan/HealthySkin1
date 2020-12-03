package com.healthy.skincare;

import com.healthy.skincare.web.Ingredient;
import lombok.Data;

import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
// to chyba powonno być tak, że jest safe product bez nextwanted, nextunwanted i jest design  z nimi.
@Data
public class SafeProduct {
    private Long id;
    private String name;
    private String brand;

    private int comedogenic;
    private int irritation;
    private int safety;
    // była lista składników, ale to mi się nie chce tak dodawać, wieć niech będzie lista id
    // zawsze mogę dopisać w javascripcie opcję , żeby przycisk dało się nacisnąć jeśli wszystkie 3 pola są wypełnione
    // to chyba powinno być na id skladnika więc string który jest id
    private List<Ingredient> unwanted;
    private List<Ingredient> wanted;
    private String nextWanted;
    private String nextUnwanted;
    private  List<String> warnings;

}