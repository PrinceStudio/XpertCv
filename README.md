# 💼 XpertCV – AI-Powered Resume Analyzer
**XpertCV** is a powerful command-line application that helps HR managers and recruiters quickly upload, analyze, rank, and export CVs based on job descriptions using AI-assisted scoring.
> ⚡ Built in Java with Apache Tika, SQLite, and mock NLP — simple, fast, and extensible.
---
## 🚀 Features

- ✅ Upload and parse PDF/DOCX CVs
- ✅ Add/manage job descriptions
- ✅ AI-powered CV ranking based on keyword relevance + experience
- ✅ View and export results (TXT/CSV)
- ✅ Delete individual or all CVs
- ✅ Search CVs by keywords, score, or filename
- ✅ CLI menu-driven, no GUI required

---

## 🛠️ Tech Stack

| Component        | Technology     |
|------------------|----------------|
| Language         | Java 17+       |
| Parsing Engine   | Apache Tika    |
| Database         | SQLite         |
| Logging          | Custom Logger  |
| Build Tool       | Maven          |
| UI               | CLI-based      |

---

## 📦 Installation

### 1. Clone the repository

```bash
git clone https://github.com/PrinceStudio/XpertCv
cd xpertcv
```

### 2. Build the project

```bash
mvn clean install
```

### 3. Run the application

```bash
mvn exec:java -Dexec.mainClass="com.xpertcv.XpertCv"
```

---

## 📂 Folder Structure

```
📁 /src
  └── /com/xpertcv/
       ├── XpertCv.java              # Main CLI Controller
       ├── UploadCv.java             # CV Upload logic
       ├── CvParser.java             # Extracts CV text
       ├── CvRanking.java            # Scores and ranks CVs
       ├── GenerateResult.java       # Exports and displays reports
       ├── JobDescriptionManager.java# Manage job descriptions
       ├── DatabaseManager.java      # SQLite DB operations
       ├── Logger.java               # Logging system
```

---

## 📖 How to Use

After running the program, you’ll see a CLI menu like this:

```
===== XPERT CV ANALYZER MENU =====
1. Upload and Process CVs
2. Add Job Description
3. Analyze & Rank CVs
4. Generate Reports
5. Export Reports
6. Delete CV
7. View CVs
8. Manage Job Descriptions
9. Search CVs
0. Exit
```

### 💡 Common Use Flow

1. **Upload CVs**  
   Place your PDF/DOCX CVs in a folder → Select option 1 → Enter folder path

2. **Add a Job Description**  
   Select option 2 → Paste the job description → Type `END` to finish

3. **Analyze CVs**  
   Option 3 → Select job ID → CVs will be scored based on relevance + experience

4. **View Rankings / Reports**  
   Use options 4 or 5 to view or export results

5. **Delete CVs**  
   Option 6 → Choose delete by ID or delete all

---

## 🧠 How Scoring Works

- **Keywords** from the job description are matched in each CV
- **Date ranges** (e.g., `2016 - 2020`, `2018 to Present`) are parsed to calculate total experience
- Final score = **Keyword Relevance + Experience Weight**

> Example: 70% match with 4 years → Score = 70 + (4 * 5) = 90

---

## 📁 Supported CV Formats

- `.pdf`
- `.docx`

---

## 🛡️ Logging

All logs are saved in:

```
xpertcv.log
```

Including user actions, errors, and system events.

---

## 📤 Export Options

Reports can be exported in:
- 📄 TXT format
- 📊 CSV format (for Excel)

---

## 🙋 FAQ


> ❓ How is AI scoring handled?  
It uses a **mock AI analyzer** to simulate real NLP-based scoring (can be upgraded to real LLM APIs).

---

## 🧪 Want to Extend?

You can:
- Add a JavaFX UI later
- Integrate with a REST API
- Replace mock AI with GPT or custom ML model

---

## 👨‍💻 Author

Made with ❤️ by Mustansar  
📧 pmustansar@gmail.com  
🔗 [LinkedIn](https://linkedin.com/in/yourprofile)

---

## 📄 License

MIT License – use it, improve it, build on it!
