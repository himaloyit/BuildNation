# BuildNation — User Manual

BuildNation is the web console for managing a political party's members, regional
structure, development projects, funds, contractors, and work orders. This manual
covers everyday use of the console at `http://localhost:8090` (or whatever address
your administrator has given you).

---

## 1. Signing In

1. Open the console in your browser. If you aren't signed in, you'll land on the
   **Login** screen automatically.
2. Enter your **email** and **password**, then click **Log In**.
3. On success you'll land on the **Dashboard**. On failure the login form shows an
   error and lets you try again.
4. To sign out, click **Logout** in the top-right corner of the header at any time.

> **Default administrator account (development/staging only):**
> `admin@buildnation.local` / `Admin@123`. Your organization should replace this
> with real, individually-owned accounts before going live — ask your administrator
> if you don't have your own login yet.

---

## 2. Finding Your Way Around

After signing in, the screen has three parts:

- **Header** (top) — the BuildNation name, your name, and **Logout**. The ☰ icon
  next to the name collapses or expands the sidebar, useful on smaller screens.
- **Sidebar** (left) — the main menu. Items with a ▸ arrow (Region, Projects, Funds,
  Work Orders) expand into a submenu when clicked.
- **Content area** (main panel) — whatever screen you've navigated to.

### Sidebar menu

| Menu item | What it's for |
|---|---|
| **Dashboard** | At-a-glance counts and totals across the whole system. |
| **Members** | Party members: contact info, role, status, profile, communication preferences. |
| **Region** ▸ | The administrative hierarchy: Districts → Upazilas → Unions → Wards → Villages. |
| **Projects** ▸ | Categories, SubCategories, Projects, and the Priority Queue. |
| **Funds** ▸ | Funds received and how they've been allocated to projects. |
| **Contractors** | Companies/individuals who carry out work orders. |
| **Work Orders** ▸ | Work Orders, Payments, and Inspections. |
| **Audit Trail** | Read-only history of who created/changed/deleted records, and when. |

### Common patterns used on every list screen

Every list screen in BuildNation (Members, Districts, Projects, and so on) follows
the same layout, so once you've used one you know them all:

- **Filter fields / dropdowns** at the top of the screen narrow the list — for
  example, filtering Members by Role or Status, or Projects by Category. Only one
  filter can be active at a time on most screens; picking a new one clears the old.
- **Add** button (top right of the toolbar) opens a form to create a new record.
- Each row has action icons on the right:
  - ✏️ **Edit** — opens the same form pre-filled, to update the record.
  - 🔄 **Change Status** (where a lifecycle applies — Projects, Contractors, Work
    Orders, Payments, Inspections) — opens a small dialog to move the record to a
    new status.
  - 🗑️ **Delete** — asks for confirmation, then removes the record. If the backend
    refuses (for example, because other records still reference it), you'll see the
    exact reason in a notification rather than a generic error.
- **Previous / Page X of Y / Next** at the bottom pages through results. Pages are
  fetched from the server as you go — the whole list isn't loaded at once.

---

## 3. Dashboard

The Dashboard is your landing page after login. Every number on it is fetched live
from the system — nothing here is a fixed or placeholder value.

- **Overview** — total Members, Projects, Contractors, and Work Orders.
- **Region Coverage** — total Districts, Upazilas, Unions, Wards, and Villages
  currently on file.
- **Fund Summary** — Total Received, Allocated, Spent, and Remaining, added up
  across every Fund in the system.
- **Projects by Status** — how many projects currently sit in each stage of their
  lifecycle (see §5.3 for what each status means).

If any tile shows **—** instead of a number, that particular figure couldn't be
fetched at that moment (for example, a backend service was briefly unavailable);
the rest of the dashboard still works normally, and reloading the page usually
resolves it.

---

## 4. Members

**Members ▸** manages everyone in the party's records: general members,
volunteers, leaders, candidates, and donors.

### Fields

| Field | Notes |
|---|---|
| Full Name | Required. |
| Email | Required, must be unique. **Cannot be changed after creation.** |
| Phone | Optional. |
| Date of Birth, Gender, Address | Only asked at creation — captured into the member's **Profile** (see below); edited later from the Profile screen, not the main Edit form. |
| Constituency ID | Optional. A free-form reference ID — there is no separate "Constituency" list to pick from yet. |
| Role | One of: General Member, Volunteer, Leader, Admin, Candidate, Donor. Set at creation; changed afterward only via **Change Role**. |
| Position | Free text (e.g. "Ward Coordinator"), editable any time via Edit. |
| Status | One of: Active, Inactive, Blacklisted, Pending Approval, Suspended. New members start **Active**; changed afterward via **Change Status**. |

### Row actions

- **Edit** — full name, phone, position, constituency ID.
- **Change Role** / **Change Status** — the two lifecycle fields, each in their own
  small dialog.
- **Profile** — date of birth, gender, nationality, address (street/city/state/
  country/postal code), and social links (Facebook, Twitter/X, LinkedIn, Instagram,
  website). Every member has a profile automatically — there's nothing to create,
  only to fill in or update.
- **Communication Preferences** — four checkboxes: Email, SMS, WhatsApp, Phone.
  Also created automatically for every member; edit it to record how they prefer
  to be contacted.
- **Delete** — removes the member (and their profile/preferences with it).

---

## 5. Region

**Region ▸** holds the administrative hierarchy used everywhere else in the system
to say *where* something is: **District → Upazila → Union → Ward → Village.**

Each level is a simple **Name + Code** record that belongs to exactly one parent
(except Districts, which are the top level). On Upazila, Union, Ward, and Village
screens, a **Filter by (parent)** dropdown narrows the list to one parent at a
time, and the same dropdown appears in the Add/Edit form to say which parent a new
record belongs to.

You cannot delete a District/Upazila/Union/Ward that still has children under it,
or a Village/other record still referenced elsewhere (e.g. by a Project) — you'll
get a clear message naming the conflict instead of a silent failure.

---

## 6. Projects

**Projects ▸** covers development project planning: **Category → SubCategory →
Project**, plus a **Priority Queue** for ranking which projects get funded first.

### 6.1 Categories & SubCategories

Simple Name + Code records, the same pattern as Region. Every SubCategory belongs
to one Category.

### 6.2 Projects

| Field | Notes |
|---|---|
| Name | Required. |
| Description, Current Condition | Optional free text. |
| Category / SubCategory | Required. Choosing a Category filters which SubCategories are offered. |
| Village | Required — where the project is located. |
| Estimated Cost | Required, must be zero or positive. |
| Priority Score | Optional at creation; used to rank projects (see 6.4). Read-only once you're editing — change it later through **Change Priority Score** or by re-running a recalculation. |
| Submitted By | Optional free text. |
| Status | See below — changed via **Change Status**, never edited directly. |

### 6.3 Project status lifecycle

`New → Pending Approval → Approved → Allocated → Running → Inspection → Completed`
(or `Cancelled` at any point). In practice, several of these transitions happen
**automatically** as you use the Funds and Work Orders screens rather than by
manually changing status here:

- Creating a **Fund Allocation** for a project moves it to **Allocated**.
- Creating a **Work Order** for it moves it to **Running**.
- Creating an **Inspection** on its work order moves it to **Inspection**.
- Approving and paying the work order's **final payment** moves it to **Completed**.

You can still manually set New / Pending Approval / Approved / Cancelled yourself
through **Change Status**.

### 6.4 Priority Queue

**Projects ▸ Priority Queue** ranks projects competing for funding — but only
projects currently in **New**, **Pending Approval**, or **Approved** status are
eligible; anything already Allocated, Running, or beyond is left out of ranking on
purpose.

1. Optionally pick a **Category** to scope the recalculation to just that category.
2. Set/adjust **Priority Score** on the projects you care about (higher = more
   urgent) using the ✏️ action on each row.
3. Click **Recalculate Queue** and confirm. The system re-sorts every eligible
   project by score (highest first) and assigns Rank 1, 2, 3…
4. The table shows the resulting rank immediately. Ranks only change when you
   recalculate — editing a score alone does not reorder the list until you do.

---

## 7. Funds

**Funds ▸** tracks money received and how it's been committed to projects.

### 7.1 Funds

| Field | Notes |
|---|---|
| Month | Required — which month this fund applies to. |
| Fund Type | Required free text (e.g. "ADP", grant name). |
| Category / SubCategory | Optional — leave blank for a general-purpose fund, or tie it to a specific category. |
| Received Amount | Required. |

**Allocated**, **Spent**, and **Remaining** amounts are shown on the list but are
never entered directly — they're maintained automatically as allocations and
payments happen elsewhere.

### 7.2 Fund Allocations

An allocation commits part of a Fund to a specific Project.

- **Add** only — there is no Edit. To change an amount, delete the allocation and
  create a new one.
- Creating an allocation reduces the fund's **Remaining** balance and moves the
  project to **Allocated** status.
- Deleting an allocation reverses the fund balance change (it does **not** revert
  the project's status, since other progress may have happened since).
- Filter by **Fund** or by **Project** to narrow the list.

---

## 8. Contractors

**Contractors** are the companies, NGOs, committees, or individuals who carry out
work orders.

| Field | Notes |
|---|---|
| Name, Contact Number, Address | Required. |
| Type | Individual, Construction Company, NGO, or Local Committee. |
| License, Key Person Name/Contact, Bank Name/Account/Branch | Optional. |
| Status | Active, Inactive, or Blacklisted — new contractors start Active, changed afterward via **Change Status**. |

Filter by **Type** or **Status**. A blacklisted contractor should not be assigned
new work orders (this is a policy, not something the screen currently blocks).

---

## 9. Work Orders

**Work Orders ▸** is the most involved part of the system — it tracks a contractor
actually carrying out a project, being paid in installments, and being inspected.

### 9.1 Work Orders

Created against a **Project**, **Contractor**, and a specific **Fund Allocation**
(so it's clear which funding line pays for it) — these three, plus the **Amount**
and **Start/End Date**, can only be set when you create the work order; editing
afterward only lets you change the amount and dates.

**Status**: In Progress → Inspection → Completed (or Cancelled). Creating the work
order moves its project to Running; the rest of the status changes below happen
through the Payments and Inspections screens, not by hand.

### 9.2 Payments

Payments are requested against a Work Order in three possible milestones —
**Advance**, **Progress**, **Final** — each for a percentage of the work order's
total amount. The percentage across all of a work order's payments cannot exceed
100%.

- **Add** only — there is no Edit; the amount is calculated automatically from the
  work order's total and the percentage you enter, so it's never typed directly.
- **Change Status** moves a payment through **Requested → Approved → Paid** (or
  **Rejected**). The backend enforces the real business rule here: a **Final**
  payment cannot be approved until the work order has an **Approved** Inspection —
  attempting it earlier will show you that exact reason.
- Approving and paying a **Final** payment completes both the Work Order and its
  Project.
- **Delete** is blocked once a payment is Approved or Paid.

### 9.3 Inspections

Records a site inspection against a Work Order — inspector name, progress
percentage, quality, remarks, and date. Creating one moves the Work Order (and its
Project) to **Inspection** status. **Change Status** moves it to **Approved** or
**Rejected** — an Approved inspection is what unlocks a Final payment (§9.2).

---

## 10. Audit Trail

**Audit Trail** is a **read-only** log of create/update/delete actions the backend
has recorded — there's no Add, Edit, or Delete here by design; entries can only be
appended by the system itself, never edited or removed afterward.

- Search by **Entity Type** (e.g. "Project"), optionally combined with a specific
  **Entity ID**, or by **Performed By**. Leave all three blank and click **Search**
  (or just load the page) to see everything.
- Click the 👁 icon on a row to see the full before/after snapshot recorded for
  that change.

> Not every action in every module is captured yet — automatic logging is only
> wired in for the parts of the system that call it explicitly, so this list may
> be sparser than the amount of real activity in the system.

---

## 11. Tips & Troubleshooting

- **"This record cannot be modified because other records still reference it"** —
  you tried to delete something still in use elsewhere (e.g. a District with
  Upazilas under it, or a Fund with allocations against it). Remove or reassign
  the dependent records first.
- **A status-change or save fails with a message you don't recognize** — the text
  shown is the backend's own explanation, not a generic error; it will usually name
  exactly what's wrong (a missing required approval, an invalid transition, etc.).
- **A dropdown (Category, Contractor, Work Order, etc.) looks empty or incomplete**
  — these pickers currently load up to 500 records at a time; if your organization
  grows past that in a single list, tell your administrator so the picker can be
  upgraded to search-as-you-type.
- **Numbers on the Dashboard show "—"** — a live figure couldn't be fetched at that
  moment; refresh the page. The Dashboard never shows a made-up number.
- Signed in but keep landing back on the Login screen? Your session may have
  expired — sign in again.

---

*This manual covers the screens available as of the current release. As new
modules are added, this document will be extended to match.*
