# Selenium Automation Suite

This repository contains automated test cases built using **Java** and **Selenium WebDriver**. The suite is designed to validate various academic and web portal functionalities such as calendar events, dataset downloads, transcript viewing, and seat booking.

---

## 🧰 Tech Stack

![Java](https://img.shields.io/badge/Java-17%2B-%23ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Selenium](https://img.shields.io/badge/Selenium-WebDriver-%2343B02A?style=for-the-badge&logo=selenium&logoColor=white)
![TestNG](https://img.shields.io/badge/TestNG-Test%20Framework-%23FF6C37?style=for-the-badge&logo=testng&logoColor=white)
![JUnit](https://img.shields.io/badge/JUnit-Testing-%23A6192E?style=for-the-badge&logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build%20Tool-%23C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

---

## 📁 Test Files

- `BaseTest.java` – Common setup and teardown logic
- `AcademicCalendarTest.java` – Validates academic calendar entries
- `CanvasCalendarTest.java` – Checks Canvas calendar sync and data
- `DownloadDatasetTest.java` – Verifies research dataset download
- `TranscriptDownloadTest.java` – Tests PDF export of student transcripts
- `SpotInSnellTest.java` – Tests library seat booking workflow

---

## 🚀 Getting Started

1. **Clone the repository**

```bash
git clone https://github.com/YOUR_USERNAME/Selenium-Automation-Suite.git
cd Selenium-Automation-Suite
```
2. **Install dependencies**

```bash
mvn clean install
```
3. **Run tests**
   
```bash
mvn test -DsuiteXmlFile=testng.xml
```

## 👨‍💻 Developer

**Srivarini Mandali**  
🔗 [GitHub](https://github.com/srivarinimandali)


