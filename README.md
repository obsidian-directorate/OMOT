# 🕶️ OMOT — Obsidian Mobile Ops Terminal

> **Official Field Application of the Obsidian Directorate (OSD)**  
> Issued under the authority of the **Obsidian Taskforce Department**  
> For use by cleared agents within the **Obsidian Corridor** universe. 
> 

---

## 📜 Overview

**OMOT (Obsidian Mobile Ops Terminal)** is a multi-layered intelligence suite designed for field agents of the **Obsidian Directorate (OSD)**
This application enables secure mission management, encrypted communications, dossier access, and tactical field operations — all locked behind **military-grade encryption** and clearance-based access control.  

🕶️ *In-world note:* This project exists within a fictional universe. Any resemblance to real agencies, countries, or systems is coincidental.  

---

## 🧩 Core Features

### 🔑 Authentication & access control
- Cipher key (password) + biometric scanner (face/print)
- Lost credentials protocol (immersive recovery flow)
- Security breach lockdown after failed attempts
- Clearance level system (BETA, ALPHA, OMEGA, SHADOW)

### 📜 Mission Command
- Mission briefings, status tracking, and agent assignment
- Attachments (PDF, images, voice notes)
- Geofenced alerts and tactical overlays

### 🗂️ Target Dossiers
- Searchable POI database with clearance restrictions
- Agent-written intel notes
- Sync with HQ when connected

### 📡 Secure Communications
- Encrypted one-to-one messaging
- Self-destructing messages
- Covert push notifications & disguised chat mode
- Encrypted attachments (text, audio, images)

### 🗺️ Tactical Tools
- Real-time allied tracking (for ALPHA+)
- Surveillance map layers
- Offline map caching
- Mission geofencing

### 🛠️ Field Kit Arsenal
- Steganography Scanner
- OCR Extractor
- Secure Camera (AES-tagged photos)
- QR Dead Drop Scanner
- AES File Vault (256-bit)

### 🧪 Command & SHADOW Systems
- OMEGA Console: agent management, analytics, remote purge
- SHADOW Mode: restricted read-only monitored access
- Rogue tagging and full surveillance logging

### 🛡️ Security Enhancements
- Full database encryption (SQLCipher)
- Panic Mode (silent wipe with decoy PIN)
- Tamper detection (root/debugger/emulator)
- Camouflage UI option (disguised as calculator/notes)
- VPN/Onion tunneling for network comms

---

## 🏗️ Technical Architecture

- **Platform:** Android (Java, Android Studio)
- **Database:** SQLite (SQLCipher for full encryption)
- **Encryption:** AES-256 for files & comms, SHA-256 + salt / bcrypt for cipher keys
- **UI/UX:**
    - Dark mode only
    - Fonts: Orbitron, Roboto Mono, Share Tech Mono, Audiowide, Exo 2, Russo One, VT323, Major Mono Display, Rajdhani
    - Terminal-style transitions, cinematic feedback
- **Modules:**
    - `Agents` / `ClearanceLevels`
    - `Missions` / `AgentMissions`
    - `Dossiers`
    - `SecureCommunications`
    - `TacticalUpdates`
    - `SystemLogs`  

---

## ⚙️ Setup & Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your_username/OMOT.git
   ```
2. Open in Android Studio
3. Build & run (requires Android SDK 24+)
4. On first launch, register as a new Agent with your codename and cipher key.

---

## 🔒 Security Notice

- This project is fictional and not intended for real-world operational security.
- Do not store sensitive personal information.
- All cryptography is for demonstration and immersive purposes inside the Obsidian Corridor universe.

---

## 📖 License

This project is licensed under the MIT license.

---

### 🕶️ In-World Motto

"In the dark corridors of power, only the Obsidian Directorate sees all."