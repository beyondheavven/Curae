package com.vention.medfast.test.model;

import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.labresult.model.LabResult;
import com.vention.medfast.labtest.model.LabTest;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tests")
@PrimaryKeyJoinColumn(name = "appointment_id")
@NoArgsConstructor
public class Test extends Appointment{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    @OneToMany(mappedBy = "testAppointment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LabResult> labResults;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LabTest> labTests;

}
