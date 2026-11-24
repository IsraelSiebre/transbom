---
version: alpha
name: "Comet"
website: "https://www.comet.com"
description: >-
  An ML experiment tracking and AI observability platform whose marketing site pairs a deep navy-black canvas with a periwinkle-violet accent (wired as --wp--preset--color--accent) and a lavender-gray secondary tone that appears more often than the accent itself. Roboto is the sole typeface, deployed from a 58px hero h1 at weight 600 down to 14px body at weight 400, with Roboto Mono carrying code examples and large numeric counters. The page uses 12px as its default card radius and 9999px pills for CTA buttons, anchoring a small-step-plus-pill binary radius system. A warm orange-red gradient ("opik") decorates the sub-product Opik's identity strip.

seo:
  title: "Comet DESIGN.md: Periwinkle on Navy, Roboto Mono Stats"
  metaDescription: "Comet's deep navy canvas, periwinkle accent and lavender-gray secondary, Roboto with Roboto Mono counters and 12px cards. For React and AI tools."
  highlights:
    - "Periwinkle accent over lavender secondary — the periwinkle accent (87 uses) is outpaced by the lavender-gray secondary (972 uses), inverting the usual primary-dominant convention"
    - "Roboto Mono for hero numbers — Roboto Mono at 50px / weight 700 renders large social-proof counters (19,000+ users, 150,000+ runs), not just code blocks"
    - "Sub-product Opik gradient — a warm orange-to-red linear gradient (--gradient-opik) marks the Opik AI observability layer distinctly from the parent Comet brand"
    - "12px default card radius, 9999px CTA pill — binary radius system with no middle tier between the conservative 12px content surface and the fully-rounded pill CTA"
    - "Lavender hairlines everywhere — the lavender-gray secondary appears 474 times as a border tone, creating a distinctly soft blue-gray perimeter around every card and button"
  tags:
    - "AI & LLM Platforms"
    - "Analytics & Data"
  lastUpdated: "2026-05-19"
  author:
    name: "Dov Azencot"
    url: "https://x.com/dovazencot"
  opening: |
    Comet's marketing site runs on a deep navy-black canvas with a periwinkle-violet accent and a lavender-gray secondary that, unusually, outpaces the accent in raw frequency — 972 uses of lavender-gray versus 87 uses of the periwinkle accent. The secondary tone is not a faded version of the accent; it is a distinct color occupying the muted-text, border, and secondary-button roles simultaneously. The effect is a page that feels cooler and more restrained than competitors like Weights & Biases (which uses brighter gold on black) or MLflow (which sits on white). The periwinkle accent is reserved for CTAs, active highlights, and gradient stop-points; the lavender-gray handles everything in between.

    The typography system is fully Roboto — no display-family switch, no editorial serif. What distinguishes it is the specific deployment of Roboto Mono for the social-proof counter section: "19,000+ GitHub Stars", "150,000+ Users", and "19,000+ Tests" render at 50px / weight 700 in Roboto Mono, making the numbers read as precise machine-generated output rather than marketing copy. This is the inverse of most platforms that render large numbers in a display sans at maximum weight. Roboto's regular body at 18–24px / weight 400–600 handles all editorial sections.

    The sub-product Opik — Comet's open-source AI observability layer — has its own visual signature: a warm orange-to-red gradient (wired as --gradient-opik) that decorates its header strip, distinct from the periwinkle-violet of the parent brand. The two identities share the same canvas, type system, and radius scale, but the gradient marks the boundary clearly. Feed this file to an AI coding tool and it reproduces the periwinkle-lavender dark-navy system, the Roboto Mono counter section, and the Opik gradient strip.
  related:
    - href: "/design"
      title: "Browse all design systems"
      description: "The full directory of DESIGN.md files on shadcn.io, with live mockups for each."
    - href: "https://www.comet.com"
      title: "Comet — official site"
      description: "Comet's public marketing site — the source of truth for the live tokens captured in this file."
    - href: "https://github.com/google-labs-code/design.md"
      title: "The DESIGN.md specification"
      description: "Google Labs' open spec for machine-readable design system files — the format this page is built on."
  questions:
    - id: "primary-color"
      title: "What is Comet's primary brand color?"
      answer: "Comet's brand accent is periwinkle violet (wired as --wp--preset--color--accent). It appears 87 times across the page — 38 as text, 10 as background, 39 as border — and serves as the CTA button background, active tab highlight, gradient stop-point, and inline text emphasis. However, the lavender-gray secondary (--wp--preset--color--secondary) is a more frequent color with 972 occurrences (494 text, 474 border, 4 bg), occupying secondary text, all card and button border outlines, and hover states. The system is unusual in that the secondary is not a tint of the primary — it is an independent cool-gray color family that happens to share the blue-violet hue angle."
    - id: "typography"
      title: "What typeface does Comet use, and what are substitutes?"
      answer: "Comet runs Roboto for all display and body surfaces, and Roboto Mono for code blocks and the social-proof numeric counters. The hero h1 sits at 58px / weight 600 / line-height 65px. Section h2 display ranges from 34px / weight 600 to 40px / weight 600 depending on section importance. Body copy runs at 18–24px / weight 400. The most distinctive typography moment is the social-proof counter section at Roboto Mono 50px / weight 700 — the monospace grid reinforces the 'precise machine measurement' positioning. Roboto is open-source (Google Fonts). For Roboto Mono, JetBrains Mono or IBM Plex Mono are direct substitutes at comparable sizes. Inter can substitute for Roboto at the body tier."
    - id: "opik-gradient"
      title: "What is the Opik gradient and when should it appear?"
      answer: "The Opik gradient (--gradient-opik) is a warm orange-to-red linear gradient sweeping at 280 degrees. It appears exclusively in the header strip and identity elements for Opik, Comet's open-source AI observability sub-product. The gradient is the visual boundary between the parent Comet brand (periwinkle-violet on dark navy) and the Opik product identity. It should not be used in Comet's general marketing chrome, pricing sections, or navigation — it is a product-specific gradient reserved for Opik-branded moments. The CSS also includes a separate heading-highlight gradient (linear-gradient at 90 degrees from periwinkle to deep navy) for accent text in dark headings."
    - id: "radius-scale"
      title: "What is Comet's corner-radius philosophy?"
      answer: "Comet runs a binary radius system: a conservative small-step tier for content surfaces, and a full pill for CTAs. The dominant card radius is 12px (26 occurrences) — applied to feature cards, testimonial blocks, and code-demo panels. Secondary surfaces use 8px (15 occurrences) and 10px (5 occurrences). The primary CTA button uses 9999px (19 occurrences) — a full pill rendering. There is also a 32px rounded container (2 occurrences) for larger promotional blocks and a 100% radius for avatar and logo circles (12 occurrences). The skip from 12px to 9999px is deliberate — no 20/24px middle tier — making the pill button the only soft-rounded surface in an otherwise conservatively-cornered system."
    - id: "use-in-project"
      title: "Can I use this DESIGN.md to build my own ML platform marketing site?"
      answer: "Yes — the file is designed to feed into Claude, Cursor, or any AI tool that reads structured design tokens. The agent will reproduce Comet's specific moves: deep navy-black canvas, periwinkle accent reserved for CTAs and highlights, lavender-gray secondary dominating borders and muted text, Roboto + Roboto Mono typography, and a binary 12px/pill radius system. The Opik gradient pattern is also captured — you can reuse the orange-to-red gradient to mark a sub-product or product tier within the same dark-canvas system. One note: the lavender-gray secondary's unusually high frequency (972 uses, more than any other color) is what gives the system its cool, restrained quality. Reducing it in favor of the periwinkle accent would shift the system toward a noisier, more aggressive branding tone."

mockups:
  - "marketing-hero"
  - "dashboard-card-grid"

colors:
  primary: "#6064ff"
  primary-dark: "#4d50e8"
  primary-light: "#7377ff"
  primary-deep: "#4145dc"
  secondary: "#afafce"
  ink: "#ffffff"
  ink-muted: "#5f677e"
  ink-soft: "#8c95a8"
  canvas: "#02081b"
  surface-1: "#1a1e2d"
  surface-2: "#262c3e"
  hairline: "#212d54"
  teal-accent: "#12a4b4"
  blue-soft: "#5899da"

typography:
  display-xl:
    fontFamily: "Roboto, sans-serif"
    fontSize: 58px
    fontWeight: 600
    lineHeight: 65px
    letterSpacing: 0
  display-md:
    fontFamily: "Roboto, sans-serif"
    fontSize: 40px
    fontWeight: 600
    lineHeight: 45px
    letterSpacing: 0
  display-sm:
    fontFamily: "Roboto, sans-serif"
    fontSize: 34px
    fontWeight: 600
    lineHeight: 38px
    letterSpacing: 0
  heading-lg:
    fontFamily: "Roboto, sans-serif"
    fontSize: 22px
    fontWeight: 500
    lineHeight: 25px
    letterSpacing: 0
  heading-md:
    fontFamily: "Roboto, sans-serif"
    fontSize: 22px
    fontWeight: 600
    lineHeight: 25px
    letterSpacing: 0
  body-xl:
    fontFamily: "Roboto, sans-serif"
    fontSize: 24px
    fontWeight: 400
    lineHeight: 38px
    letterSpacing: 0
  body-lg:
    fontFamily: "Roboto, sans-serif"
    fontSize: 20px
    fontWeight: 400
    lineHeight: 32px
    letterSpacing: 0
  body-md:
    fontFamily: "Roboto, sans-serif"
    fontSize: 18px
    fontWeight: 400
    lineHeight: 29px
    letterSpacing: 0
  body-sm:
    fontFamily: "Roboto, sans-serif"
    fontSize: 16px
    fontWeight: 400
    lineHeight: 26px
    letterSpacing: 0
  button-md:
    fontFamily: "Roboto, sans-serif"
    fontSize: 18px
    fontWeight: 700
    lineHeight: 18px
    letterSpacing: 0
  nav-link:
    fontFamily: "Roboto, sans-serif"
    fontSize: 16px
    fontWeight: 500
    lineHeight: 16px
    letterSpacing: 0
  mono-xl:
    fontFamily: "\"Roboto Mono\", monospace"
    fontSize: 50px
    fontWeight: 700
    lineHeight: 80px
    letterSpacing: 0
  mono-md:
    fontFamily: "\"Roboto Mono\", monospace"
    fontSize: 18px
    fontWeight: 400
    lineHeight: 29px
    letterSpacing: 0
  caption:
    fontFamily: "Roboto, sans-serif"
    fontSize: 14px
    fontWeight: 400
    lineHeight: 22px
    letterSpacing: 0
  label-caps:
    fontFamily: "Roboto, sans-serif"
    fontSize: 14px
    fontWeight: 600
    lineHeight: 15px
    letterSpacing: 0

rounded:
  none: "0px"
  xs: "3px"
  sm: "5px"
  md: "8px"
  lg: "10px"
  xl: "12px"
  2xl: "32px"
  pill: "9999px"
  full: "100%"

spacing:
  xs: "8px"
  sm: "12px"
  base: "16px"
  md: "20px"
  lg: "24px"
  xl: "40px"
  2xl: "50px"
  3xl: "80px"

components:
  button-primary:
    backgroundColor: "{colors.primary}"
    textColor: "{colors.ink}"
    typography: "{typography.button-md}"
    rounded: "{rounded.pill}"
    padding: "12px 20px"
    height: "52px"
    borderColor: "{colors.primary}"
  button-secondary:
    backgroundColor: "transparent"
    textColor: "{colors.ink}"
    typography: "{typography.button-md}"
    rounded: "{rounded.pill}"
    padding: "12px 20px"
    height: "52px"
    borderColor: "{colors.secondary}"
  top-nav:
    backgroundColor: "{colors.canvas}"
    textColor: "{colors.ink}"
    typography: "{typography.nav-link}"
    rounded: "{rounded.none}"
    padding: "0px 36px"
    height: "56px"
  nav-link:
    backgroundColor: "transparent"
    textColor: "{colors.ink}"
    typography: "{typography.nav-link}"
    padding: "0px 7.5px"
  hero-heading:
    backgroundColor: "transparent"
    textColor: "{colors.ink}"
    typography: "{typography.display-xl}"
    padding: "0px"
  section-heading:
    backgroundColor: "transparent"
    textColor: "{colors.ink}"
    typography: "{typography.display-md}"
    padding: "0px"
  body-paragraph:
    backgroundColor: "transparent"
    textColor: "{colors.ink}"
    typography: "{typography.body-lg}"
    padding: "0px"
  body-paragraph-muted:
    backgroundColor: "transparent"
    textColor: "{colors.ink-muted}"
    typography: "{typography.body-md}"
    padding: "0px"
  card:
    backgroundColor: "{colors.surface-1}"
    textColor: "{colors.ink}"
    typography: "{typography.heading-md}"
    rounded: "{rounded.xl}"
    padding: "40px 36px"
    borderColor: "{colors.secondary}"
  card-hover:
    backgroundColor: "{colors.surface-1}"
    textColor: "{colors.ink}"
    typography: "{typography.heading-md}"
    rounded: "{rounded.xl}"
    padding: "40px 36px"
    borderColor: "{colors.primary}"
  counter-stat:
    backgroundColor: "transparent"
    textColor: "{colors.ink}"
    typography: "{typography.mono-xl}"
    padding: "0px"
  text-input:
    backgroundColor: "transparent"
    textColor: "{colors.ink}"
    typography: "{typography.body-md}"
    rounded: "{rounded.sm}"
    padding: "12px"
    height: "48px"
    borderColor: "{colors.secondary}"
  testimonial-card:
    backgroundColor: "{colors.surface-2}"
    textColor: "{colors.ink}"
    typography: "{typography.body-md}"
    rounded: "{rounded.xl}"
    padding: "40px 36px"
    borderColor: "{colors.hairline}"
  footer:
    backgroundColor: "{colors.canvas}"
    textColor: "{colors.ink-muted}"
    typography: "{typography.body-sm}"
    padding: "50px 36px"
---

## Overview

Comet's marketing site anchors itself on a deep navy-black canvas and a periwinkle-violet accent, but the page's true chromatic workhorse is neither — it is a lavender-gray secondary tone that outpaces every other color on the page by a 10:1 margin. **Secondary-dominant branding.** The lavender-gray secondary appears 972 times across text, border, and hover roles, while the periwinkle accent appears 87 times in CTAs and highlights. This inversion — where the secondary color is more visible than the primary — produces a page that reads as deliberately cool and restrained, closer to an academic research tool than to the brighter, higher-chroma ML platforms like Weights & Biases.

The comparison to competitors is precise: Weights & Biases uses gold-yellow on black with heavy visual density; MLflow sits on white with plain navy typography; Neptune.ai uses a teal-on-white system. Comet's periwinkle-on-dark-navy with lavender-gray borders is the quietest visual approach in the ML tracking category — the color says "infrastructure for engineers" rather than "dashboard for everyone."

The Roboto Mono counter section is the single most distinctive typographic moment: "19,000+ GitHub Stars" and "150,000+ Users" rendered in monospace at 50px / weight 700, making social proof read as instrument output rather than marketing copy. No other platform in the directory uses monospace as the vehicle for social-proof numbers.

**Key Characteristics:**
- Deep navy-black canvas (`{colors.canvas}` — #02081b) — cooler and slightly bluer than Spline's pure near-black or Earthly's navy-blue canvas.
- Lavender-gray secondary (`{colors.secondary}` — #afafce) outpaces the periwinkle accent by 10:1 in total frequency — the dominant border and muted-text tone.
- Periwinkle accent (`{colors.primary}` — #6064ff) reserved for CTA fills, active tab highlights, and gradient stop-points.
- Roboto Mono at 50px / weight 700 for social-proof counters — the most distinctive typographic move in the system.
- Binary radius: 12px for content cards, 9999px for CTA pill buttons. No 20–24px middle tier.
- Sub-product Opik gradient (--gradient-opik, orange-to-red at 280°) marks the AI observability layer with a warm orange-to-red identity strip.
- Card hover state flips the lavender-gray border to periwinkle — the only hover signal in the system.

## Colors

### Brand

- **Primary** (`{colors.primary}` — #6064ff): frequency 87. Used as text (38), bg (10), border (39). The CTA button fill, active tab, inline text highlight, gradient stop. Wired as --wp--preset--color--accent.
- **Primary Dark** (`{colors.primary-dark}` — #4d50e8): frequency 5. Gradient stop in the accent-background and dark-heading-highlight gradients. Wired into --wp--preset--gradient--custom-accent-background.
- **Primary Light** (`{colors.primary-light}` — #7377ff): frequency 4. The light stop on the heading-highlight-dark gradient. Appears in gradient-only contexts.
- **Secondary** (`{colors.secondary}` — #afafce): frequency 972. Text (494), border (474), bg (4). The dominant surface-level color — card outlines, secondary button borders, muted text, hover-state border for interactive elements. Wired as --wp--preset--color--secondary and multiple button-hover variables.

### Text

- **Ink** (`{colors.ink}` — #ffffff): frequency 326. Primary heading and body text. Pure white on the dark navy canvas.
- **Ink Muted** (`{colors.ink-muted}` — #5f677e): frequency 22. Secondary body copy, footer text, nav submenu labels. Wired as --wp--preset--color--foreground and --wp--custom--color--foreground.
- **Ink Soft** (`{colors.ink-soft}` — #8c95a8): frequency 10. Tertiary text — the medium-gray body tier below muted.

### Surface

- **Canvas** (`{colors.canvas}` — #02081b): frequency 15. The primary page floor and section backgrounds. Wired as --wp--preset--color--custom-background-dark.
- **Surface-1** (`{colors.surface-1}` — #1a1e2d): frequency 15. Elevated card and tile surfaces — the card background above the deep-navy floor. Wired as --wp--preset--color--custom-alt-background-dark.
- **Surface-2** (`{colors.surface-2}` — #262c3e): frequency 9. The secondary elevated surface — testimonial cards, feature-tile overlays. Wired as --wp--custom--color--tertiary and --wp--preset--color--tertiary.

### Hairline

- **Hairline** (`{colors.hairline}` — #212d54): frequency 22, all as border. The structural card-divider tone — a mid-navy between the canvas and the surface-1 card color. Wired as --wp--preset--color--custom-decorative-border-dark.

### Accent

- **Teal Accent** (`{colors.teal-accent}` — #12a4b4): frequency 8. Text (4) and border (4). Used for the teal accent tier in feature labels and category chips — not a primary or secondary brand tone, but present in the product-feature grid.
- **Blue Soft** (`{colors.blue-soft}` — #5899da): frequency 18. Text (9) and border (9). A sky-blue accent used in secondary link and highlight contexts — the softer blue in the periwinkle family.

## Typography

### Font Families

The system runs **Roboto** for every editorial surface — display, heading, body, button, nav, caption, label — with no second display family. The fallback stack is `sans-serif` only. **Roboto Mono** handles code blocks and the social-proof counter section. The Roboto Mono counter section (`{typography.mono-xl}` — 50px / weight 700 / line-height 80px) is the standout: rendering "19,000+" in a fixed-width font at this size makes the number read as a precisely measured instrument value rather than a marketing claim.

### Hierarchy

| Token | Size | Weight | Line Height | Use |
|---|---|---|---|---|
| `{typography.display-xl}` | 58px | 600 | 65px | Hero h1 ("The Fastest Path to Agents That Work") |
| `{typography.display-md}` | 40px | 600 | 45px | Section h2 display tier |
| `{typography.display-sm}` | 34px | 600 | 38px | Sub-section display ("Log & organize every step your agent takes") |
| `{typography.heading-lg}` | 22px | 500 | 25px | Card h3 titles |
| `{typography.heading-md}` | 22px | 600 | 25px | Bolder card h3 variant |
| `{typography.body-xl}` | 24px | 400 | 38px | Large lead paragraph / testimonial pull-quote |
| `{typography.body-lg}` | 20px | 400 | 32px | Feature row lead copy |
| `{typography.body-md}` | 18px | 400 | 29px | Default running text (most-used body size) |
| `{typography.body-sm}` | 16px | 400 | 26px | Secondary body, footer copy |
| `{typography.button-md}` | 18px | 700 | 18px | CTA button labels |
| `{typography.nav-link}` | 16px | 500 | 16px | Top-nav link labels |
| `{typography.mono-xl}` | 50px | 700 | 80px | Social-proof counters in Roboto Mono |
| `{typography.mono-md}` | 18px | 400 | 29px | Code block body text |
| `{typography.caption}` | 14px | 400 | 22px | Sub-labels, metadata |
| `{typography.label-caps}` | 14px | 600 | 15px | Uppercase section category labels |

### Principles

The display tier tops at weight 600 — Comet avoids the 700+ display-weight arms race. The hero h1 at 58px / 600 is the loudest typographic moment, and its authority comes from size rather than weight. The monospace counter at 50px / 700 is actually heavier but narrower — the fixed-width rhythm of Roboto Mono reads as technical precision rather than marketing boldness. Body copy runs at weight 400 throughout.

### Note on Font Substitutes

Roboto is open-source (Google Fonts). For the display and body tier, **Inter** maps directly at the same sizes and weights. For Roboto Mono at the counter tier, **JetBrains Mono** or **IBM Plex Mono** at weight 700 read similarly — the key is preserving the fixed-width character at 50px. The counters should remain in a monospace family; switching to a proportional sans at that size removes the "instrument output" reading.

## Layout

### Spacing System

- **Base unit:** 8px (xs).
- **Tokens:** `{spacing.xs}` 8px · `{spacing.sm}` 12px · `{spacing.base}` 16px · `{spacing.md}` 20px · `{spacing.lg}` 24px · `{spacing.xl}` 40px · `{spacing.2xl}` 50px · `{spacing.3xl}` 80px.
- **Card internal padding:** `{spacing.xl}` × `{spacing.2xl}` (40×36px) on feature cards and testimonial blocks.
- **Section padding (vertical):** 50–80px between major sections.
- **Nav padding:** 0×36px horizontal; the nav bar sits at 56px height.

### Grid & Container

- **Max content width:** ~865px content column (`--wp--style--global--content-size: 865px`), 1228px wide-size for full-bleed sections.
- **Hero:** two-column layout at desktop — text column left (~40%) with the hero h1, sub-copy, and primary CTA; visual column right with animated product screenshot.
- **Feature sections:** alternating left/right two-column layout for the product feature rows.
- **Counter strip:** horizontal row of three Roboto Mono counter blocks (19,000+ Stars / 150,000+ Users / 19,000+ Tests).
- **Social proof:** 3-column customer testimonial grid at desktop.

### Rhythm

The page uses **consistent dark canvas throughout** — no section inversion to white, no alternating light/dark bands. Section separation comes from slight surface elevation (`{colors.surface-1}` card fills on `{colors.canvas}` floor) and from the periwinkle accent reappearing as section heading emphasis. The monotone-dark rhythm reinforces the "infrastructure tool" positioning — it reads as a dashboard, not a campaign.

## Elevation

The system uses **tonal lift** with a subtle drop shadow declared in CSS presets:

- **Flat (no shadow):** hero, nav, editorial text sections, footer.
- **Tonal lift:** `{colors.surface-1}` and `{colors.surface-2}` cards on `{colors.canvas}` — the 10–20 lightness-point gap between canvas and surface reads as elevation.
- **CTA pill shadow:** `--wp--preset--shadow--natural: 6px 6px 9px rgba(0, 0, 0, 0.2)` — a soft black shadow on the primary CTA pill, barely visible against the dark canvas. One of the few shadow declarations in the system.
- **Deep shadow:** `--wp--preset--shadow--deep: 12px 12px 50px rgba(0, 0, 0, 0.4)` — declared but not visually prominent on the marketing surfaces.

## Shapes

The radius scale is **binary**: conservative small-step for content, full pill for CTAs.

- `{rounded.none}` 0px — hero section edges, full-width banner strips.
- `{rounded.xs}` 3px — the smallest chip (1 occurrence).
- `{rounded.sm}` 5px — form input borders (`--wp--custom--button--border--radius: 4px`, captured as 5px).
- `{rounded.md}` 8px — secondary card tier and tag chips (15 occurrences).
- `{rounded.lg}` 10px — medium content tiles (5 occurrences).
- `{rounded.xl}` 12px — the dominant card radius (26 occurrences — used on feature cards, testimonial cards, code demo blocks).
- `{rounded.2xl}` 32px — large promotional container (2 occurrences).
- `{rounded.pill}` 9999px — primary and secondary CTA buttons (19 occurrences). The single warm surface in the system.
- `{rounded.full}` 100% — avatar and logo circles (12 occurrences).

The 12px-to-9999px jump is the defining shape decision — there is no 20 or 24px transitional tier. Every content surface uses 12px; every interactive CTA uses the full pill.

## Components

**`button-primary`** — Periwinkle `{colors.primary}` fill, white text, Roboto 18px / 700, full-pill `{rounded.pill}` 9999px radius, 12×20px padding, 52px height. "Try Comet Free" and "Log" are canonical instances.

**`button-secondary`** — Transparent fill, white text, 2px lavender-gray `{colors.secondary}` border, same pill radius and padding. Used for secondary CTAs adjacent to the primary.

**`top-nav`** — Dark canvas surface, 56px height, 0×36px padding. Comet wordmark flush left, product links (Platform, Pricing, Customers, Blog, Docs, Company, Log in), "Try for Free" primary CTA flush right.

**`nav-link`** — Transparent, white text at Roboto 16px / 500, 0×7.5px padding. The nav links render in pure white rather than the brand accent — ink is the nav default, not the violet.

**`hero-heading`** — Pure white text, Roboto 58px / 600 / line-height 65px. The hero h1 on the dark navy canvas.

**`section-heading`** — White text, Roboto 40px or 34px / 600 depending on section prominence.

**`body-paragraph`** — White text at Roboto 20px / 400. Feature row lead copy.

**`body-paragraph-muted`** — Muted `{colors.ink-muted}` text at Roboto 18px / 400. Secondary descriptions, footer categories.

**`card`** — `{colors.surface-1}` fill, white text, 2px lavender-gray `{colors.secondary}` border, 12px radius, 40×36px padding. The default feature card — lavender border in resting state.

**`card-hover`** — Same card but border flips to periwinkle `{colors.primary}` on hover. The only hover signal in the system; the color shift from lavender to violet is the interaction cue.

**`counter-stat`** — Transparent background, white text, Roboto Mono 50px / 700 / line-height 80px. The social-proof counter row (19,000+, 150,000+, 19,000+).

**`testimonial-card`** — `{colors.surface-2}` (#262c3e) fill, white text, mid-navy `{colors.hairline}` border, 12px radius, 40×36px padding.

**`text-input`** — Transparent fill, white text, 2px `{colors.secondary}` border, 5px radius, 12px padding, 48px height. Email / form inputs in the contact and demo-request flows.

**`footer`** — Deep canvas `{colors.canvas}` floor, muted text, Roboto 16px / 400, 50×36px padding.

## Do's and Don'ts

**Do** let the lavender-gray secondary (`{colors.secondary}`) carry the border and muted-text roles. Its 10:1 frequency advantage over the periwinkle accent is not a mistake — the system achieves its cool, restrained quality precisely because the secondary dominates the visible border surface. Replacing lavender-gray borders with periwinkle borders would produce a page that reads as aggressive rather than technical.

**Do** keep the Roboto Mono counter section at 50px / weight 700. The monospace-at-scale move is the system's most distinctive typographic choice; swapping to Roboto Regular at 50px removes the instrument-output reading and turns the counter into a conventional marketing number.

**Do** use the card hover state exactly as specified — lavender-gray border at rest, periwinkle border on hover. This is the only interactive-state signal in the system. Introducing a background-color hover (e.g., a purple fill) would add a second hover cue on top of the border signal.

**Do** reserve the Opik gradient (--gradient-opik) exclusively for Opik-branded identity moments — header strips, product-tier labels, and Opik-specific CTAs. It reads as a warm energetic burst against the cold navy canvas; deploying it elsewhere weakens the sub-product identity distinction.

**Don't** introduce a light-mode or white-canvas variant without also defining alternative lavender-gray and canvas tokens. The lavender-gray secondary (`{colors.secondary}`) is calibrated for legibility against the dark navy canvas; it would read as washed-out and low-contrast against a white surface.

**Don't** use `{colors.teal-accent}` (#12a4b4) or `{colors.blue-soft}` (#5899da) as primary or secondary brand accents. Both appear at low frequency (8 and 18 occurrences respectively) in specific product-surface contexts; treating them as general-purpose accents would create a five-color system with no clear hierarchy.

**Don't** add a mid-range card radius between 12px and the full pill. The binary system is its own design statement — conservative content surfaces vs. warm pill CTAs. Adding a 20px or 24px transitional card variant dissolves the categorical distinction.

**Don't** render the hero h1 above weight 600. The display tier is calibrated at size 58px / weight 600 — bumping to 700 or 800 on Roboto at that size would tip from confident to heavy and lose the academic-instrument aesthetic.

## Known Gaps

- **Light mode:** the captured marketing surface is dark-only. Comet's product dashboard may carry a light-mode token set not represented here.
- **Hover and focus matrix:** the card hover state (lavender border → periwinkle border) is captured; the full interactive-state matrix (button active, input focus ring, nav link hover, link underline) is not fully visible on the static captured surface.
- **Form validation states:** `{component.text-input}` captures the resting state; error, success, and focus-ring variants are not exposed.
- **Opik sub-product token set:** the Opik gradient (--gradient-opik), the Opik amber-orange, and the Opik signal-red form the beginning of a sub-brand palette, but the complete Opik design system is not captured here.
- **Motion and animations:** the hero product screenshot animates, and the counter section may use count-up animation. Easing curves and duration values are not represented.
- **Product surfaces:** this DESIGN.md captures the comet.com marketing site. The Comet experiment tracking dashboard and the Opik platform UI carry their own denser token systems for charts, experiment cards, metric plots, and annotation tools.
- **WordPress-scoped variables:** Comet's site is built on WordPress; many CSS custom properties are prefixed --wp--preset--* and --wp--custom--*, which limits portability. The tokens in this file strip the WP namespace for direct usability in React projects.
