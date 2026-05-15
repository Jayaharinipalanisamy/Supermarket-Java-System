import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SupermarketUI extends JFrame {

    private JTextField searchField;
    private JLabel totalLabel;

    private DefaultTableModel productModel, cartModel;

    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<CartItem> cart = new ArrayList<>();

    private double total = 0;

    public SupermarketUI() {

        setTitle("Smart Supermarket POS");
        setSize(1000, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Sample products
        products.add(new Product(1, "Rice", 50));
        products.add(new Product(2, "Milk", 25));
        products.add(new Product(3, "Bread", 30));
        products.add(new Product(4, "Sugar", 45));

        // SEARCH BAR
        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.BOLD, 16));
        add(searchField, BorderLayout.NORTH);

        // PRODUCT TABLE
        productModel = new DefaultTableModel(new String[]{"ID", "Name", "Price"}, 0);
        JTable productTable = new JTable(productModel);
        loadProducts("");

        // CART TABLE
        cartModel = new DefaultTableModel(new String[]{"Name", "Price", "Qty", "Subtotal"}, 0);
        JTable cartTable = new JTable(cartModel);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(productTable),
                new JScrollPane(cartTable)
        );

        add(splitPane, BorderLayout.CENTER);

        // BOTTOM PANEL
        JPanel bottom = new JPanel(new BorderLayout());

        totalLabel = new JLabel("Total: ₹0");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JButton checkoutBtn = new JButton("Checkout (F2)");

        bottom.add(totalLabel, BorderLayout.WEST);
        bottom.add(checkoutBtn, BorderLayout.EAST);

        add(bottom, BorderLayout.SOUTH);

        // SEARCH FUNCTION
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                loadProducts(searchField.getText());
            }
        });

        // ADD TO CART
        productTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = productTable.getSelectedRow();

                String name = productModel.getValueAt(row, 1).toString();
                double price = Double.parseDouble(productModel.getValueAt(row, 2).toString());

                String qtyStr = JOptionPane.showInputDialog("Enter Quantity:");
                if (qtyStr == null || qtyStr.isEmpty()) return;

                int qty = Integer.parseInt(qtyStr);

                CartItem item = new CartItem(name, price, qty);
                cart.add(item);

                double subtotal = item.getSubtotal();
                cartModel.addRow(new Object[]{name, price, qty, subtotal});

                total += subtotal;
                totalLabel.setText("Total: ₹" + total);
            }
        });

        // REMOVE FROM CART
        cartTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = cartTable.getSelectedRow();

                double sub = Double.parseDouble(cartModel.getValueAt(row, 3).toString());

                total -= sub;
                cart.remove(row);
                cartModel.removeRow(row);

                totalLabel.setText("Total: ₹" + total);
            }
        });

        // CHECKOUT
        checkoutBtn.addActionListener(e -> generateInvoice());

        // SHORTCUT (F2)
        checkoutBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("F2"), "checkout");

        checkoutBtn.getActionMap().put("checkout", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                generateInvoice();
            }
        });
    }

    private void loadProducts(String keyword) {
        productModel.setRowCount(0);

        for (Product p : products) {
            if (p.getName().toLowerCase().contains(keyword.toLowerCase())) {
                productModel.addRow(new Object[]{p.getId(), p.getName(), p.getPrice()});
            }
        }
    }

    private void generateInvoice() {
        StringBuilder bill = new StringBuilder("------ BILL ------\n");

        for (CartItem c : cart) {
            bill.append(c.getName())
                .append(" x ").append(c.getQuantity())
                .append(" = ₹").append(c.getSubtotal())
                .append("\n");
        }

        bill.append("\nTotal: ₹").append(total);

        JTextArea area = new JTextArea(bill.toString());
        JOptionPane.showMessageDialog(this, new JScrollPane(area));

        // RESET
        cart.clear();
        cartModel.setRowCount(0);
        total = 0;
        totalLabel.setText("Total: ₹0");
    }
}
