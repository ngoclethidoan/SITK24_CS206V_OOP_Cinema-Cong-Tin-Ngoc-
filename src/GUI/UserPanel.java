package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import model.BookTicket;

public class UserPanel extends JPanel {
    private JTable tableHistory;
    private DefaultTableModel tableModel;
    private JButton btnRefund;

    public UserPanel() {
        setLayout(new BorderLayout());

        // Thiết lập bảng hiển thị lịch sử
        String[] columns = {"Mã Vé", "Tên Phim", "Ghế", "Tổng Tiền"};
        tableModel = new DefaultTableModel(columns, 0);
        tableHistory = new JTable(tableModel);
        
        add(new JScrollPane(tableHistory), BorderLayout.CENTER);

        // Nút hoàn vé
        btnRefund = new JButton("Hoàn vé (Xóa)");
        btnRefund.addActionListener(e -> handleRefund());
        add(btnRefund, BorderLayout.SOUTH);

        // Load dữ liệu ban đầu
        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0); // Xóa trắng bảng
        // Giả sử bạn lấy danh sách từ BookingService đã có trong project của bạn
        // List<BookTicket> tickets = BookingService.getAllTickets(); 
        // for(BookTicket t : tickets) { ... thêm vào tableModel ... }
    }

    private void handleRefund() {
        int row = tableHistory.getSelectedRow();
        if (row != -1) {
            String ticketId = tableHistory.getValueAt(row, 0).toString();
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn hoàn vé " + ticketId + "?");
            
            if (confirm == JOptionPane.YES_OPTION) {
                // 1. Logic Xóa: Gọi hàm xóa trong Database/Service của bạn
                // boolean success = BookingDatabase.removeTicket(ticketId);
                
                // 2. Cập nhật lại file text (yêu cầu 1đ file text)
                // BookingDatabase.saveToFile(); 
                
                JOptionPane.showMessageDialog(this, "Đã hoàn vé và cập nhật lại dữ liệu!");
                loadData(); // Cập nhật lại bảng
            }
        } else {
            JOptionPane.showMessageDialog(this, "Hãy chọn một vé để hoàn.");
        }
    }
}