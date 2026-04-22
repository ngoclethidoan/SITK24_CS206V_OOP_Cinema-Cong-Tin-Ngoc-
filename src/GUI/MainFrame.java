
package GUI;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {

    // Khai báo các thành phần là biến thành viên
    private JPanel topPanel;
    private JPanel searchPanel;
    private JPanel actionPanel;
    private JScrollPane movieScrollPane;
    private JPanel movieContainer; // Cần biến này để thay đổi số cột
    
    //adjust image size
    private ImageIcon resizeImage(String path, int width, int height) {
    try {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage();
        // Căn chỉnh ảnh theo tỉ lệ của Card
        Image newImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    } catch (Exception e) {
        System.out.println("Không tìm thấy ảnh tại: " + path);
        return null; // Trả về null nếu lỗi
    }
}

    public MainFrame(Dimension size) {
        // 1. Configure main frame
        setTitle("CNTCinema");
        this.setSize(size);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Use null layout to set fixed size and position for panels
        setLayout(null); 

        // 2. Add Panel on the top (contain user information)
        topPanel = createTopPanel();
        add(topPanel);

        // Thanh công cụ phía trên (Search, Cart, Settings) ---
        searchPanel = createSearchPanel();
        add(searchPanel);

        actionPanel = createActionPanel();
        add(actionPanel);

        // 3. Thêm vùng nội dung chính
        movieScrollPane = createMovieArea();
        add(movieScrollPane);

        // --- CƠ CHẾ CẬP NHẬT LAYOUT VÀ TĂNG SỐ CỘT LINH HOẠT ---
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getContentPane().getWidth();
                int h = getContentPane().getHeight();

                // Cập nhật vị trí các Panel
                topPanel.setBounds(10, 10, 220, 70);
                searchPanel.setBounds((w - 400) / 2, 20, 400, 40);
                actionPanel.setBounds(w - 170, 20, 150, 40);
                movieScrollPane.setBounds(10, 100, w - 20, h - 110);

                // TÍNH TOÁN SỐ CỘT: Giả sử mỗi card rộng khoảng 200px (bao gồm gap)
                // Thay vì làm card to ra, ta tăng số cột lên
                int newColumns = Math.max(2, w / 220); 
                GridLayout layout = (GridLayout) movieContainer.getLayout();
                layout.setColumns(newColumns);

                revalidate();
                repaint();
            }
        });
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)); 
        topPanel.setBackground(new Color(236, 240, 241)); 
        topPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JLabel lblAvatar = new JLabel("IMG");
        lblAvatar.setPreferredSize(new Dimension(50, 60)); 
        lblAvatar.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        lblAvatar.setHorizontalAlignment(JLabel.CENTER);

        JPanel userPanel = new JPanel(new GridLayout(2, 1));
        userPanel.setOpaque(false); 

        JLabel lblUserName = new JLabel("User: admin");
        lblUserName.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel lblRole = new JLabel("Role: Administrator");
        lblRole.setFont(new Font("Arial", Font.ITALIC, 12));
        lblRole.setForeground(Color.GRAY);

        userPanel.add(lblUserName);
        userPanel.add(lblRole);

        topPanel.add(lblAvatar);
        topPanel.add(userPanel);

        return topPanel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);
        JLabel lblSearchIcon = new JLabel("🔍");
        JTextField txtSearch = new JTextField(" Tìm kiếm phim...");
        txtSearch.setForeground(Color.GRAY);
        panel.add(lblSearchIcon, BorderLayout.EAST);
        panel.add(txtSearch, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panel.setOpaque(false);
        JButton btnCart = new JButton("🛒"); 
        btnCart.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 25));
        JButton btnSettings = new JButton("\u22EE"); 
        btnSettings.setFont(new Font("Segoe UI Symbol", Font.BOLD, 20)); 

        btnCart.setBorderPainted(false);
        btnCart.setContentAreaFilled(false);
        btnSettings.setBorderPainted(false);
        btnSettings.setContentAreaFilled(false);
        btnCart.setFocusPainted(false);
        btnSettings.setFocusPainted(false);

        panel.add(btnCart);
        panel.add(btnSettings);
        return panel;
    }

    private JScrollPane createMovieArea() {
        // Lưu JPanel vào biến movieContainer để đổi số cột sau này
        movieContainer = new JPanel(new GridLayout(0, 4, 20, 20)); 
        movieContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        movieContainer.setBackground(Color.WHITE);
        
        //
        java.util.List<model.Film> films = database.FilmDatabase.getAllFilms();

//        for (int i = 1; i <= 16; i++) {
//            movieContainer.add(createMovieCard("Bộ phim số " + i));
//        }
        
        for (model.Film f : films) {
        // Mỗi card bây giờ sẽ nhận một object Film thay vì chỉ là String
        movieContainer.add(createMovieCard(f)); 
    }
        movieContainer.revalidate();
        movieContainer.repaint();
        JScrollPane scrollPane = new JScrollPane(movieContainer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Phim Đang Chiếu"));
        
        return scrollPane;
    }

    private JPanel createMovieCard(model.Film film) {
        
        JPanel card = new JPanel(new BorderLayout());
        card.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hiện hình bàn tay khi di chuột vào card
        card.setPreferredSize(new Dimension(180, 250));
        card.setMaximumSize(new Dimension(180, 250));
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        

        // 1. XỬ LÝ PHẦN POSTER
        JLabel lblPoster = new JLabel();
        lblPoster.setHorizontalAlignment(JLabel.CENTER);
        
        // Gọi hàm resizeImage với đường dẫn từ đối tượng film
        // Kích thước ảnh nên nhỏ hơn card một chút để chừa chỗ cho tiêu đề (ví dụ 180x210)
        ImageIcon icon = resizeImage(film.getImagePath(), 180, 210);
        
        if (icon != null) {
            lblPoster.setIcon(icon);
        } else {
            // Nếu không tìm thấy ảnh, hiển thị nền xám và chữ POSTER như cũ
            lblPoster.setText("NO IMAGE");
            lblPoster.setBackground(Color.DARK_GRAY);
            lblPoster.setOpaque(true);
            lblPoster.setForeground(Color.WHITE);
        }

        // 2. XỬ LÝ PHẦN TIÊU ĐỀ PHIM
        JLabel lblTitle = new JLabel(film.getTitle(), JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 13));
        lblTitle.setPreferredSize(new Dimension(0, 40)); // Chiều cao cho phần chữ

        // Thêm các thành phần vào card
        card.add(lblPoster, BorderLayout.CENTER);
        card.add(lblTitle, BorderLayout.SOUTH);

        // 3. XỬ LÝ SỰ KIỆN (Giữ nguyên logic của bạn)
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            }
            
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Dimension currentSize = MainFrame.this.getSize();
                int extendedState = MainFrame.this.getExtendedState();
                Point location = MainFrame.this.getLocation();

                FilmFrame detail = new FilmFrame(
                    film.getTitle(), 
                    film.getDirector(),
                    film.getCast(),
                    film.getDuration(),
                    film.getSummary(),
                    currentSize
                );
                
                detail.setLocation(location);
                detail.setExtendedState(extendedState);
                detail.setVisible(true);
                MainFrame.this.dispose(); 
            }
        });
        
        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame(new Dimension(1000, 750)).setVisible(true);
        });
    }
}