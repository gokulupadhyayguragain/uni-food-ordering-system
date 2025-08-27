# 🍕 University Food Ordering System

A comprehensive Java Swing-based food ordering system designed specifically for university environments. This desktop application facilitates food ordering for students, faculty, and staff while providing management tools for vendors, delivery personnel, and administrators.

## 🌟 Key Features

### 👨‍🎓 Customer Features
- **User Registration & Login**: Secure account creation and authentication
- **Menu Browsing**: Browse food items by category, vendor, and price
- **Order Management**: Place orders, track status, and view order history
- **Payment Integration**: Multiple payment methods and order tracking
- **Favorites**: Save frequently ordered items
- **Rating & Reviews**: Rate food items and leave feedback
- **Order History**: View past orders and reorder functionality
- **Real-time Notifications**: Order status updates and delivery notifications

### 🏪 Vendor Features
- **Vendor Dashboard**: Comprehensive business management interface
- **Menu Management**: Add, edit, remove food items with pricing
- **Order Processing**: Accept, prepare, and manage incoming orders
- **Inventory Management**: Track ingredient availability and stock levels
- **Sales Analytics**: View sales reports, popular items, and revenue tracking
- **Customer Feedback**: Monitor ratings and reviews
- **Promotional Tools**: Create discounts and special offers
- **Order History**: Track all processed orders and customer interactions

### 🚚 Delivery Runner Features
- **Order Assignment**: View assigned delivery orders
- **Route Optimization**: Efficient delivery route planning
- **Status Updates**: Update order status (picked up, in transit, delivered)
- **Delivery History**: Track completed deliveries and earnings
- **Performance Metrics**: View delivery statistics and ratings
- **Real-time Communication**: Contact customers and vendors

### 👨‍💼 Admin Features
- **System Management**: Overall system administration and monitoring
- **User Management**: Manage customers, vendors, and delivery personnel
- **Order Oversight**: Monitor all orders across the platform
- **Analytics Dashboard**: System-wide analytics and reporting
- **Quality Control**: Monitor service quality and resolve disputes
- **Financial Management**: Payment processing and fee management
- **System Configuration**: Manage system settings and parameters

## 🏗️ System Architecture

### Technology Stack
- **Language**: Java (JDK 8+)
- **GUI Framework**: Java Swing with custom components
- **IDE**: NetBeans IDE 12+
- **Build System**: Apache Ant
- **Data Storage**: File-based storage (text files)
- **Architecture**: Model-View-Controller (MVC) pattern

### Project Structure
```
uni-food-ordering-system/
├── build.xml                          # Ant build configuration
├── manifest.mf                        # JAR manifest file
├── nbproject/                         # NetBeans project configuration
│   ├── build-impl.xml                # NetBeans build implementation
│   ├── genfiles.properties           # Generated files configuration
│   ├── project.properties            # Project settings
│   ├── project.xml                   # Project metadata
│   └── private/                      # Private NetBeans settings
├── src/                              # Source code directory
│   ├── Main.java                     # Application entry point
│   ├── JavaFiles/                    # Core application classes
│   │   ├── LandingPage.java          # Main application window
│   │   ├── LoginSignup.java          # Authentication system
│   │   ├── Customers.java            # Customer interface and logic
│   │   ├── Vendors.java              # Vendor management system
│   │   ├── DeliveryRunners.java      # Delivery personnel interface
│   │   └── Admin.java                # Administrative functions
│   └── TextFiles/                    # Data storage files
│       └── Orders.txt                # Order data storage
├── build/                            # Compiled classes
│   └── classes/                      # Compiled .class files
└── README.md                         # Project documentation
```

### Class Hierarchy and Design Patterns

#### Core Classes
- **Main.java**: Application entry point with main method
- **LandingPage.java**: Central hub for navigation and user selection
- **LoginSignup.java**: Handles user authentication and registration

#### User Role Classes
- **Customers.java**: Customer interface with ordering functionality
- **Vendors.java**: Vendor management with menu and order handling
- **DeliveryRunners.java**: Delivery personnel interface and tracking
- **Admin.java**: Administrative functions and system oversight

#### Design Patterns Used
- **Singleton Pattern**: For database connections and system configuration
- **Observer Pattern**: For real-time order status updates
- **Strategy Pattern**: For different payment methods
- **Factory Pattern**: For creating different user types
- **MVC Pattern**: Separation of concerns in UI and business logic

## 🚀 Getting Started

### Prerequisites
- **Java Development Kit (JDK)**: Version 8 or higher
- **NetBeans IDE**: Version 12 or later (recommended)
- **Apache Ant**: For building the project
- **Operating System**: Windows, macOS, or Linux

### Installation & Setup

1. **Clone the Repository**
   ```bash
   git clone https://github.com/gokulupadhyayguragain/uni-food-ordering-system.git
   cd uni-food-ordering-system
   ```

2. **Open in NetBeans**
   - Launch NetBeans IDE
   - File → Open Project
   - Navigate to the cloned directory and select the project
   - Wait for NetBeans to load the project

3. **Build the Project**
   ```bash
   # Using Ant (command line)
   ant clean compile
   
   # Or in NetBeans
   # Right-click project → Clean and Build
   ```

4. **Run the Application**
   ```bash
   # Using Ant
   ant run
   
   # Or in NetBeans
   # Right-click project → Run
   # Or press F6
   ```

### Alternative Setup (Command Line)

1. **Compile the Source**
   ```bash
   javac -cp src src/Main.java src/JavaFiles/*.java
   ```

2. **Create JAR File**
   ```bash
   jar cfm UniFoodOrdering.jar manifest.mf -C src .
   ```

3. **Run the Application**
   ```bash
   java -cp . Main
   # Or
   java -jar UniFoodOrdering.jar
   ```

## 🎯 Usage Guide

### For Customers
1. **Registration**: Create a new account with student/faculty ID
2. **Login**: Access your account using credentials
3. **Browse Menu**: Explore available food items by category or vendor
4. **Place Order**: Add items to cart and proceed to checkout
5. **Payment**: Choose payment method and confirm order
6. **Track Order**: Monitor order status in real-time
7. **Receive Delivery**: Get notification when order arrives

### For Vendors
1. **Vendor Registration**: Register your food business
2. **Menu Setup**: Add food items with descriptions, prices, and images
3. **Order Management**: Monitor incoming orders and update status
4. **Inventory Control**: Manage ingredient availability
5. **Analytics**: View sales reports and customer feedback
6. **Promotions**: Create special offers and discounts

### For Delivery Personnel
1. **Registration**: Sign up as a delivery runner
2. **Order Assignment**: Receive delivery assignments
3. **Route Planning**: Optimize delivery routes
4. **Status Updates**: Update order status during delivery
5. **Completion**: Mark orders as delivered and collect feedback

### For Administrators
1. **System Login**: Access admin panel with credentials
2. **User Management**: Monitor and manage all user accounts
3. **Order Oversight**: Track system-wide order activity
4. **Analytics**: Generate reports and system statistics
5. **Quality Control**: Resolve disputes and maintain service quality

## 📊 Data Management

### File-Based Storage System
The application uses a simple file-based storage system for data persistence:

#### Orders.txt Structure
```
OrderID|CustomerID|VendorID|Items|TotalAmount|Status|Timestamp|DeliveryAddress
```

#### Data Operations
- **Create**: Add new records to text files
- **Read**: Parse text files to retrieve data
- **Update**: Modify existing records
- **Delete**: Remove records (mark as inactive)

### Future Database Integration
The system is designed to easily migrate to a database system:
- **MySQL**: For production environments
- **SQLite**: For lightweight deployments
- **PostgreSQL**: For advanced features

## 🖥️ User Interface Design

### Design Principles
- **User-Centered Design**: Intuitive interface for all user types
- **Consistent Layout**: Uniform design across all windows
- **Accessibility**: Clear fonts, proper contrast, and keyboard navigation
- **Responsive Elements**: Adaptable to different screen resolutions

### Key UI Components
- **Custom Buttons**: Styled buttons with hover effects
- **Data Tables**: Sortable and filterable data display
- **Progress Indicators**: Visual feedback for long operations
- **Modal Dialogs**: Context-specific popup windows
- **Navigation Panels**: Easy switching between different sections

### Color Scheme & Branding
- **Primary Color**: University brand colors
- **Secondary Colors**: Complementary colors for actions
- **Success/Error Colors**: Clear visual feedback
- **Neutral Colors**: Professional appearance

## 🔒 Security Features

### Authentication & Authorization
- **User Authentication**: Secure login with password validation
- **Role-Based Access**: Different privileges for different user types
- **Session Management**: Automatic logout after inactivity
- **Data Validation**: Input validation to prevent malicious data

### Data Security
- **File Permissions**: Restricted access to data files
- **Input Sanitization**: Clean user inputs to prevent injection
- **Error Handling**: Graceful error handling without exposing system info
- **Audit Trail**: Log important system activities

## 🧪 Testing

### Test Cases
1. **User Registration & Login**
   - Valid/invalid credentials
   - Password strength validation
   - Account creation process

2. **Order Management**
   - Order placement workflow
   - Order status updates
   - Payment processing

3. **Menu Management**
   - Add/edit/delete food items
   - Inventory updates
   - Price modifications

4. **Delivery Tracking**
   - Assignment algorithm
   - Status update notifications
   - Delivery completion

### Testing Strategy
- **Unit Testing**: Individual component testing
- **Integration Testing**: System component interaction
- **User Acceptance Testing**: End-user workflow validation
- **Performance Testing**: System load and response time

## 📈 Performance Optimization

### Application Performance
- **Lazy Loading**: Load data only when needed
- **Caching**: Cache frequently accessed data
- **Efficient Algorithms**: Optimized search and sort operations
- **Memory Management**: Proper object cleanup and garbage collection

### UI Performance
- **Event Handling**: Efficient event processing
- **Component Reuse**: Reusable UI components
- **Thread Management**: Background processing for heavy operations
- **Resource Management**: Efficient image and resource loading

## 🔧 Configuration

### Application Settings
```properties
# config.properties
app.title=University Food Ordering System
app.version=1.0.0
max.order.items=20
delivery.radius.km=5
session.timeout.minutes=30
```

### Customization Options
- **University Branding**: Logo, colors, and theme customization
- **Business Rules**: Delivery time limits, order minimums
- **Payment Methods**: Configure available payment options
- **Notification Settings**: Email and SMS notification preferences

## 🚀 Future Enhancements

### Planned Features
- **Mobile Application**: Android/iOS companion apps
- **Real-time Chat**: Customer-vendor communication
- **GPS Tracking**: Real-time delivery tracking
- **Analytics Dashboard**: Advanced reporting and insights
- **Integration APIs**: Third-party service integration
- **Machine Learning**: Recommendation system for food items

### Technical Improvements
- **Database Migration**: Move from file-based to database storage
- **Web Interface**: Web-based version of the application
- **Cloud Deployment**: Cloud-based hosting and scaling
- **Microservices**: Modular architecture for better scalability

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines
- Follow Java coding conventions
- Add comments for complex logic
- Update documentation for new features
- Test thoroughly before submitting

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Gokul Upadhyay Guragain (NP069822)**
- GitHub: [@gokulupadhyayguragain](https://github.com/gokulupadhyayguragain)
- Project: [University Food Ordering System](https://github.com/gokulupadhyayguragain/uni-food-ordering-system)

## 📞 Support

For support and questions:
- Create an issue on GitHub
- Check the documentation in the wiki
- Contact the development team

## 🙏 Acknowledgments

- University administration for project requirements
- Java Swing community for UI components
- NetBeans IDE for development environment
- Testing team for quality assurance
- Open source community for inspiration and resources

---

*Connecting university communities through food* 🍕🎓
