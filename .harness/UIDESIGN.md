Follow these precise specifications for theme, design system, structure, and user flow:

### 1. Core Design Philosophy & Audience
- Target Audience: Young adults, students, fresh graduates, and early-career professionals in Malaysia. The design must resonate with a tech-savvy, aspirational crowd.
- Vibe: A "simplicity-first" approach that balances the credibility of a real finance tool with the energetic, engaging feel of a gamified rewards system. It must not look like a dry corporate banking app or a shallow marketing landing page.
- Layout: Clean layouts, structured pages, ample whitespace, and an overall calm, step-based onboarding feel.

### 2. Design System & Tokens (Material 3)
Please implement these styling guidelines using Material 3 design tokens:
- Typography: Clean, modern sans-serif fonts (e.g., Inter or Roboto). Use clear hierarchical scaling (Large Title for screen naming, Medium/Regular Body for forms and labels).
- Color Palette: 
  * Primary/Branding: A trustworthy deep indigo or slate charcoal for structural elements and main text to maintain financial credibility.
  * Accents (The "Gamified" elements): A vibrant mint or emerald green representing financial growth/savings, paired with playful coral or gold accent highlights to draw the eye to points, rewards, and actionable items.
  * Backgrounds: Light, clean, and distraction-free backgrounds that give elements plenty of breathing room.
- Components: Reusable Material 3 OutlinedTextFields with smooth corner radiuses (e.g., 12dp to 16dp) for an approachable, modern geometric look.

### 3. Language & Copy Style
- Use clear, non-technical, friendly language throughout the screens.
- Avoid rigid terms. For buttons, use action-oriented copy like "Log In" or "Create Account".

### 4. Screen Structure & Form Fields

#### LoginScreen
- Layout: Vertically centered layout with the clean Mantapp logo or styled typography at the top.
- Fields: 
  * Email (with appropriate keyboard type and input validation support).
  * Password (with visual toggle for show/hide password functionality).
- Actions: 
  * Primary Button: "Log In" (vibrant primary color fill).
  * Secondary Text Link: "Don't have an account? Sign Up".

#### RegistrationScreen
- Layout: A clean, step-based form layout with a clear back arrow or navigation path leading back to the Login screen.
- Fields:
  * Full Name / Username (to personalize future dashboard welcomes).
  * Email Address.
  * Password (with secure entry validation).
- Actions:
  * Primary Button: "Create Account" (vibrant primary color fill).
  * Secondary Text Link: "Already have an account? Log In".

### 5. Interaction and State Handling
- Explicitly support states for: Empty/Invalid Input (show clear, calm inline error indicators under fields), Loading (smooth Material 3 CircularProgressIndicator on primary buttons during mock authentication), Error (friendly snackbar or container), and Success.
- Ensure form validation prevents empty submissions.
- Post-Registration Flow: Ensure that completing a successful registration triggers an auto-login function, establishing the local session state before smoothly routing the user forward.
