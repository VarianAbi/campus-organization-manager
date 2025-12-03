package com.campusorg.patterns;

import com.campusorg.models.*;
import com.campusorg.patterns.composite.*;
import com.campusorg.patterns.factory.MemberFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test untuk Factory Pattern - MemberFactory
 */
public class MemberFactoryTest {

    @Test
    public void testCreateStaffMuda() {
        // Arrange & Act
        Member member = MemberFactory.createMember("Staff Muda", null, "Budi Santoso", "ID-001");
        
        // Assert
        assertNotNull("Member tidak boleh null", member);
        assertTrue("Harus instance dari StaffMuda", member instanceof StaffMuda);
        assertEquals("Nama harus sesuai", "Budi Santoso", member.getName());
        assertEquals("Role harus 'Staff Muda'", "Staff Muda", member.getRole());
    }

    @Test
    public void testCreateStaffAhli() {
        // Arrange & Act
        Member member = MemberFactory.createMember("Staff Ahli", null, "Dr. Andi", "ID-002");
        
        // Assert
        assertNotNull("Member tidak boleh null", member);
        assertTrue("Harus instance dari StaffAhli", member instanceof StaffAhli);
        assertEquals("Nama harus sesuai", "Dr. Andi", member.getName());
        assertEquals("Role harus 'Staff Ahli'", "Staff Ahli", member.getRole());
    }

    @Test
    public void testCreateOfficeHolder() {
        // Arrange & Act
        Member member = MemberFactory.createMember("Pejabat Struktural", "Ketua Himpunan", "Siti Nurhaliza", "ID-003");
        
        // Assert
        assertNotNull("Member tidak boleh null", member);
        assertTrue("Harus instance dari OfficeHolder", member instanceof OfficeHolder);
        assertEquals("Nama harus sesuai", "Siti Nurhaliza", member.getName());
        assertEquals("Role harus sesuai jabatan", "Ketua Himpunan", member.getRole());
    }

    @Test
    public void testCreateWithUnknownType() {
        // Arrange & Act
        Member member = MemberFactory.createMember("Unknown Type", null, "John Doe", "ID-999");
        
        // Assert
        assertNotNull("Member tidak boleh null (fallback ke StaffMuda)", member);
        assertTrue("Fallback harus ke StaffMuda", member instanceof StaffMuda);
    }

    @Test
    public void testCreateMultipleMembersAreDifferentInstances() {
        // Arrange & Act
        Member member1 = MemberFactory.createMember("Staff Muda", null, "Person A", "ID-101");
        Member member2 = MemberFactory.createMember("Staff Muda", null, "Person B", "ID-102");
        
        // Assert
        assertNotSame("Harus objek berbeda", member1, member2);
        assertNotEquals("Nama harus berbeda", member1.getName(), member2.getName());
    }
}
