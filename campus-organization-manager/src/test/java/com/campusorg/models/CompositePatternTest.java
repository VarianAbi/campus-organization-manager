package com.campusorg.models;

import com.campusorg.patterns.composite.*;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Unit test untuk Composite Pattern - Division & Member
 */
public class CompositePatternTest {

    private Division rootDiv;
    private Division childDiv1;
    private Division childDiv2;
    private Member member1;
    private Member member2;

    @Before
    public void setUp() {
        rootDiv = new Division("HIMAKOM");
        childDiv1 = new Division("BPH");
        childDiv2 = new Division("Departemen");
        member1 = new StaffMuda("Alice", "001");
        member2 = new OfficeHolder("Bob", "002", "Ketua");
    }

    @Test
    public void testDivisionCanAddMember() {
        // Act
        rootDiv.addMember(member1);
        
        // Assert
        assertEquals("Division harus punya 1 member", 1, rootDiv.getMembers().size());
        assertTrue("Member harus ada dalam list", rootDiv.getMembers().contains(member1));
    }

    @Test
    public void testDivisionCanAddDivision() {
        // Act
        rootDiv.addMember(childDiv1);
        
        // Assert
        assertEquals("Division harus punya 1 child division", 1, rootDiv.getMembers().size());
        assertTrue("Child division harus ada dalam list", rootDiv.getMembers().contains(childDiv1));
    }

    @Test
    public void testCompositeStructure() {
        // Arrange & Act - Buat struktur hierarki
        rootDiv.addMember(childDiv1);
        rootDiv.addMember(childDiv2);
        childDiv1.addMember(member1);
        childDiv2.addMember(member2);
        
        // Assert
        assertEquals("Root harus punya 2 children", 2, rootDiv.getMembers().size());
        assertEquals("ChildDiv1 harus punya 1 member", 1, childDiv1.getMembers().size());
        assertEquals("ChildDiv2 harus punya 1 member", 1, childDiv2.getMembers().size());
    }

    @Test
    public void testDivisionRemoveMember() {
        // Arrange
        rootDiv.addMember(member1);
        rootDiv.addMember(member2);
        
        // Act
        rootDiv.removeMember(member1);
        
        // Assert
        assertEquals("Division harus punya 1 member setelah remove", 1, rootDiv.getMembers().size());
        assertFalse("Member yang di-remove tidak boleh ada", rootDiv.getMembers().contains(member1));
    }

    @Test
    public void testOrgComponentInterface() {
        // Assert - Division dan Member implement OrgComponent
        assertTrue("Division harus implement OrgComponent", rootDiv instanceof OrgComponent);
        assertTrue("Member harus implement OrgComponent", member1 instanceof OrgComponent);
    }

    @Test
    public void testDivisionGetName() {
        // Assert
        assertEquals("Nama division harus sesuai", "HIMAKOM", rootDiv.getName());
    }

    @Test
    public void testDivisionGetRole() {
        // Assert
        assertEquals("Role division harus 'Divisi'", "Divisi", rootDiv.getRole());
    }

    @Test
    public void testMemberGetRole() {
        // Assert
        assertEquals("Role StaffMuda harus sesuai", "Staff Muda", member1.getRole());
        assertEquals("Role OfficeHolder harus sesuai jabatan", "Ketua", member2.getRole());
    }

    @Test
    public void testProkerManagement() {
        // Arrange
        Proker proker1 = new Proker("Webinar AI", "Seminar teknologi AI", "Alice", "Bob", "Planning", 25, "BPH");
        Proker proker2 = new Proker("Lomba Coding", "Kompetisi pemrograman", "Charlie", "David", "On Progress", 50, "BPH");
        
        // Act
        rootDiv.addProker(proker1);
        rootDiv.addProker(proker2);
        
        // Assert
        assertEquals("Division harus punya 2 proker", 2, rootDiv.getProkerList().size());
        assertTrue("Proker 1 harus ada dalam list", rootDiv.getProkerList().contains(proker1));
    }

    @Test
    public void testEmptyDivision() {
        // Arrange
        Division emptyDiv = new Division("Empty Division");
        
        // Assert
        assertEquals("Division baru harus kosong", 0, emptyDiv.getMembers().size());
        assertEquals("Proker list harus kosong", 0, emptyDiv.getProkerList().size());
    }

    @Test
    public void testMultilevelHierarchy() {
        // Arrange
        Division level1 = new Division("Level 1");
        Division level2 = new Division("Level 2");
        Division level3 = new Division("Level 3");
        
        // Act - Buat 3 level hierarki
        level1.addMember(level2);
        level2.addMember(level3);
        level3.addMember(member1);
        
        // Assert
        assertEquals("Level 1 harus punya 1 child", 1, level1.getMembers().size());
        assertEquals("Level 2 harus punya 1 child", 1, level2.getMembers().size());
        assertEquals("Level 3 harus punya 1 member", 1, level3.getMembers().size());
    }

    @Test
    public void testMemberNotifications() {
        // Arrange
        member1.update("Test notification");
        member1.update("Another notification");
        
        // Assert
        assertEquals("Member harus punya 2 notifikasi", 2, member1.getNotifications().size());
    }

    @Test
    public void testMemberUangKas() {
        // Arrange
        int initialKas = member1.getUangKas();
        
        // Act
        member1.bayarKas(50000);
        member1.bayarKas(25000);
        
        // Assert
        assertEquals("Uang kas harus bertambah 75000", initialKas + 75000, member1.getUangKas());
    }
}
