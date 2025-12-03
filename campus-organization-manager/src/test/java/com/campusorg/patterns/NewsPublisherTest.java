package com.campusorg.patterns;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import com.campusorg.patterns.composite.Member;
import com.campusorg.patterns.composite.OfficeHolder;
import com.campusorg.patterns.composite.StaffAhli;
import com.campusorg.patterns.composite.StaffMuda;
import com.campusorg.patterns.observer.NewsPublisher;

/**
 * Unit test untuk Observer Pattern - NewsPublisher
 */
public class NewsPublisherTest {
    
    private NewsPublisher publisher;
    private Member member1;
    private Member member2;
    private Member member3;

    @Before
    public void setUp() {
        publisher = new NewsPublisher();
        member1 = new StaffMuda("Alice", "ID-001");
        member2 = new StaffAhli("Bob", "ID-002");
        member3 = new OfficeHolder("Charlie", "ID-003", "Ketua");
    }

    @Test
    public void testSubscribeSingleMember() {
        // Act
        publisher.subscribe(member1);
        publisher.notifyAll("Test message");
        
        // Assert
        assertEquals("Member harus menerima 1 notifikasi", 1, member1.getNotifications().size());
        assertTrue("Notifikasi harus mengandung pesan", 
                   member1.getNotifications().get(0).contains("Test message"));
    }

    @Test
    public void testSubscribeMultipleMembers() {
        // Arrange
        publisher.subscribe(member1);
        publisher.subscribe(member2);
        publisher.subscribe(member3);
        
        // Act
        publisher.notifyAll("Rapat besok pukul 10.00");
        
        // Assert
        assertEquals("Member 1 harus menerima notifikasi", 1, member1.getNotifications().size());
        assertEquals("Member 2 harus menerima notifikasi", 1, member2.getNotifications().size());
        assertEquals("Member 3 harus menerima notifikasi", 1, member3.getNotifications().size());
    }

    @Test
    public void testUnsubscribeMember() {
        // Arrange
        publisher.subscribe(member1);
        publisher.subscribe(member2);
        
        // Act
        publisher.unsubscribe(member1);
        publisher.notifyAll("Pesan setelah unsubscribe");
        
        // Assert
        assertEquals("Member 1 tidak boleh menerima notifikasi setelah unsubscribe", 
                     0, member1.getNotifications().size());
        assertEquals("Member 2 harus tetap menerima notifikasi", 
                     1, member2.getNotifications().size());
    }

    @Test
    public void testNotifyAllWithoutSubscribers() {
        // Act - tidak ada subscriber
        publisher.notifyAll("Pesan tanpa subscriber");
        
        // Assert - tidak ada exception, program tetap jalan
        assertTrue("Test berhasil jika tidak ada exception", true);
    }

    @Test
    public void testMultipleNotifications() {
        // Arrange
        publisher.subscribe(member1);
        
        // Act
        publisher.notifyAll("Pesan pertama");
        publisher.notifyAll("Pesan kedua");
        publisher.notifyAll("Pesan ketiga");
        
        // Assert
        assertEquals("Member harus menerima 3 notifikasi", 3, member1.getNotifications().size());
    }

    @Test
    public void testNotificationContent() {
        // Arrange
        publisher.subscribe(member1);
        String expectedMessage = "Deadline proker minggu depan!";
        
        // Act
        publisher.notifyAll(expectedMessage);
        
        // Assert
        String notification = member1.getNotifications().get(0);
        assertTrue("Notifikasi harus berisi nama member", notification.contains(member1.getName()));
        assertTrue("Notifikasi harus berisi role member", notification.contains(member1.getRole()));
        assertTrue("Notifikasi harus berisi pesan", notification.contains(expectedMessage));
    }

    @Test
    public void testSubscribeSameMemberTwice() {
        // Arrange & Act
        publisher.subscribe(member1);
        publisher.subscribe(member1); // Subscribe lagi
        publisher.notifyAll("Test duplicate");
        
        // Assert
        // Member akan menerima 2 kali karena di-subscribe 2 kali
        assertEquals("Member menerima notifikasi sesuai jumlah subscribe", 
                     2, member1.getNotifications().size());
    }
}
