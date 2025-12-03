package com.campusorg.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.campusorg.patterns.composite.Division;
import com.campusorg.patterns.composite.Member;
import com.campusorg.patterns.composite.StaffMuda;
import com.campusorg.patterns.observer.NewsPublisher;
import com.campusorg.patterns.singleton.OrgManager;

/**
 * Unit test untuk Singleton Pattern - OrgManager
 */
public class OrgManagerTest {

    @Test
    public void testGetInstanceReturnsSameInstance() {
        // Arrange & Act
        OrgManager instance1 = OrgManager.getInstance();
        OrgManager instance2 = OrgManager.getInstance();
        
        // Assert
        assertNotNull("Instance pertama tidak boleh null", instance1);
        assertNotNull("Instance kedua tidak boleh null", instance2);
        assertSame("Harus instance yang sama (Singleton)", instance1, instance2);
    }

    @Test
    public void testOrgManagerInitializesRootOrganization() {
        // Arrange & Act
        OrgManager manager = OrgManager.getInstance();
        Division root = manager.getRoot();
        
        // Assert
        assertNotNull("Root organization harus ada", root);
        assertEquals("Nama root harus HIMAKOM (Pusat)", "HIMAKOM (Pusat)", root.getName());
    }

    @Test
    public void testDivisionMapContainsExpectedDivisions() {
        // Arrange
        OrgManager manager = OrgManager.getInstance();
        
        // Act & Assert - Test beberapa divisi key
        assertNotNull("BPH Inti harus ada", manager.getDivisionByName("BPH Inti"));
        assertNotNull("MPA harus ada", manager.getDivisionByName("MPA"));
        assertNotNull("Biro KESRA harus ada", manager.getDivisionByName("Biro KESRA"));
        assertNotNull("Dept. KOMINFO harus ada", manager.getDivisionByName("Dept. KOMINFO"));
    }

    @Test
    public void testGetDivisionNamesReturnsArray() {
        // Arrange
        OrgManager manager = OrgManager.getInstance();
        
        // Act
        String[] divisionNames = manager.getDivisionNames();
        
        // Assert
        assertNotNull("Array divisi tidak boleh null", divisionNames);
        assertTrue("Harus ada divisi (minimal 1)", divisionNames.length > 0);
    }

    @Test
    public void testGetDivisionByNameReturnsCorrectDivision() {
        // Arrange
        OrgManager manager = OrgManager.getInstance();
        
        // Act
        Division bph = manager.getDivisionByName("BPH Inti");
        
        // Assert
        assertNotNull("Division BPH Inti harus ditemukan", bph);
        assertEquals("Nama divisi harus sesuai", "BPH Inti", bph.getName());
    }

    @Test
    public void testGetDivisionByNameReturnsNullForNonExistent() {
        // Arrange
        OrgManager manager = OrgManager.getInstance();
        
        // Act
        Division nonExistent = manager.getDivisionByName("Divisi Tidak Ada");
        
        // Assert
        assertNull("Division yang tidak ada harus return null", nonExistent);
    }

    @Test
    public void testPublisherIsInitialized() {
        // Arrange & Act
        OrgManager manager = OrgManager.getInstance();
        NewsPublisher publisher = manager.getPublisher();
        
        // Assert
        assertNotNull("NewsPublisher harus ter-inisialisasi", publisher);
    }

    @Test
    public void testRegisterMemberAddsMemberToDivision() {
        // Arrange
        OrgManager manager = OrgManager.getInstance();
        Division bph = manager.getDivisionByName("BPH Inti");
        int initialSize = bph.getMembers().size();
        
        Member newMember = new StaffMuda("Test Member", "TEST-001");
        
        // Act
        manager.registerMember(bph, newMember);
        
        // Assert
        assertEquals("Member harus bertambah 1", initialSize + 1, bph.getMembers().size());
    }
}
