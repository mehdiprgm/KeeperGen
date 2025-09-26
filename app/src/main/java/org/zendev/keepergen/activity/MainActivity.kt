package org.zendev.keepergen.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import org.zendev.keepergen.R
import org.zendev.keepergen.databinding.ActivityMainBinding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import org.zendev.keepergen.adapter.recyclerview.AccountAdapter
import org.zendev.keepergen.adapter.recyclerview.BankCardAdapter
import org.zendev.keepergen.adapter.recyclerview.ContactAdapter
import org.zendev.keepergen.adapter.recyclerview.NoteAdapter
import org.zendev.keepergen.viewmodel.DatabaseViewModel
import org.zendev.keepergen.database.entity.Account
import org.zendev.keepergen.database.entity.BankCard
import org.zendev.keepergen.database.entity.Contact
import org.zendev.keepergen.database.entity.Note
import org.zendev.keepergen.dialog.BottomDialogAccountDetails
import org.zendev.keepergen.dialog.BottomDialogBankCardDetails
import org.zendev.keepergen.dialog.BottomDialogContactDetails
import org.zendev.keepergen.dialog.BottomDialogNewItem
import org.zendev.keepergen.dialog.BottomDialogNoteDetails
import org.zendev.keepergen.dialog.DialogType
import org.zendev.keepergen.dialog.Dialogs
import org.zendev.keepergen.tools.changeTheme
import org.zendev.keepergen.tools.copyTextToClipboard
import org.zendev.keepergen.tools.disableScreenPadding
import org.zendev.keepergen.tools.getAllViews
import org.zendev.keepergen.tools.isDarkTheme
import org.zendev.keepergen.tools.lockActivityOrientation
import org.zendev.keepergen.tools.preferencesName
import org.zendev.keepergen.tools.selectedItems
import org.zendev.keepergen.tools.selectedViews
import org.zendev.keepergen.tools.shareText

class MainActivity : AppCompatActivity(), View.OnClickListener,
    NavigationView.OnNavigationItemSelectedListener {
    private lateinit var b: ActivityMainBinding
    private lateinit var databaseViewModel: DatabaseViewModel

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    private lateinit var accountAdapter: AccountAdapter
    private lateinit var bankCardAdapter: BankCardAdapter
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var noteAdapter: NoteAdapter

    private var isSelectionModeActivated = false

    private var currentIndex = 1
    private var backPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        disableScreenPadding(b.root)

        ViewCompat.setOnApplyWindowInsetsListener(b.layNavigationBottom) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = insets.bottom)

            windowInsets
        }

        setupNavigationDrawer()
        setupViewModel()
        setupSearchBar()
        setupNavigationHeader()

        setOnBackPressedListener()

        b.imgMenu.setOnClickListener(this)

        b.btnAdd.setOnClickListener(this)
        b.btnDelete.setOnClickListener(this)
        b.btnShare.setOnClickListener(this)
        b.btnCopy.setOnClickListener(this)

        b.layAccounts.setOnClickListener(this)
        b.layBankCards.setOnClickListener(this)
        b.layContacts.setOnClickListener(this)
        b.layNotes.setOnClickListener(this)

        b.layAccounts.performClick()

        lockActivityOrientation(this)
    }

    override fun onPause() {
        super.onPause()
        disableSelectionMode()
    }

    /* save the current fragment index before activity destroyed for android ui changes */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentIndex", 0)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnAdd -> {
                val rotate180Reverse = AnimationUtils.loadAnimation(this, R.anim.rotate_180_reverse)
                rotate180Reverse.duration = 300
                view.startAnimation(rotate180Reverse)

                val newItemDialog = BottomDialogNewItem(this)
                newItemDialog.show(supportFragmentManager, "New Item")
            }

            R.id.btnDelete -> {
                if (selectedItems.isNotEmpty()) {
                    val firstItem = selectedItems.first()

                    lifecycleScope.launch {
                        if (Dialogs.ask(
                                this@MainActivity,
                                R.drawable.ic_delete,
                                "Delete selected items",
                                "You have selected ${selectedItems.size} item(s) from the list.\nAre you sure you want to delete these items?\nChanges can't undo."
                            )
                        ) {
                            when (firstItem) {
                                is Account -> {
                                    for (account in selectedItems) {
                                        databaseViewModel.deleteAccount(account as Account)
                                    }
                                }

                                is BankCard -> {
                                    for (bankCard in selectedItems) {
                                        databaseViewModel.deleteBankCard(bankCard as BankCard)
                                    }
                                }

                                is Contact -> {
                                    for (contact in selectedItems) {
                                        databaseViewModel.deleteContact(contact as Contact)
                                    }
                                }

                                is Note -> {
                                    for (note in selectedItems) {
                                        databaseViewModel.deleteNote(note as Note)
                                    }
                                }
                            }

                            disableSelectionMode()
                        }
                    }
                }
            }

            R.id.btnShare -> {
                val sb = StringBuilder()

                if (selectedItems.isNotEmpty()) {
                    val firstItem = selectedItems.first()

                    /* create text to share text */
                    when (firstItem) {
                        is Account -> {
                            for (account in selectedItems) {
                                sb.append(account)
                            }
                        }

                        is BankCard -> {
                            for (bankCard in selectedItems) {
                                sb.append(bankCard)
                            }
                        }

                        is Contact -> {
                            for (contact in selectedItems) {
                                sb.append(contact)
                            }
                        }

                        is Note -> {
                            for (note in selectedItems) {
                                sb.append(note)
                            }
                        }
                    }

                    disableSelectionMode()
                    shareText(this, "Share information", sb.toString())
                }
            }

            R.id.btnCopy -> {
                val sb = StringBuilder()

                if (selectedItems.isNotEmpty()) {
                    val firstItem = selectedItems.first()

                    /* create text to share text */
                    when (firstItem) {
                        is Account -> {
                            for (account in selectedItems) {
                                sb.append(account)
                            }
                        }

                        is BankCard -> {
                            for (bankCard in selectedItems) {
                                sb.append(bankCard)
                            }
                        }

                        is Contact -> {
                            for (contact in selectedItems) {
                                sb.append(contact)
                            }
                        }

                        is Note -> {
                            for (note in selectedItems) {
                                sb.append(note)
                            }
                        }
                    }

                    disableSelectionMode()
                    copyTextToClipboard(this, "Keepergen", sb.toString())
                }
            }

            R.id.imgMenu -> {
                b.main.openDrawer(GravityCompat.START)
            }

            R.id.layAccounts -> {/* we change the currentIndex variable so the app name textview can change when loadDatatableTable function called */
                changeBottomNavigationItem(b.layAccounts)

                currentIndex = 1
                loadDatabaseTable()
            }

            R.id.layBankCards -> {
                changeBottomNavigationItem(b.layBankCards)

                currentIndex = 2
                loadDatabaseTable()
            }

            R.id.layContacts -> {
                changeBottomNavigationItem(b.layContacts)

                currentIndex = 3
                loadDatabaseTable()
            }

            R.id.layNotes -> {
                changeBottomNavigationItem(b.layNotes)

                currentIndex = 4
                loadDatabaseTable()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuProfile -> {
                Dialogs.confirm(
                    this,
                    title = "Not available",
                    message = "The sync feature is not complete and can't be used right now.",
                    DialogType.Error
                )
//                try {
//                    val pref = getSharedPreferences(preferencesName, MODE_PRIVATE)
//                    val loggedIn = pref.getBoolean("LoggedIn", false)
//
//                    if (loggedIn) {
//                        val viewModel = ViewModelProvider(this)[ApiViewModel::class.java]
//                        val loadDialog = Dialogs.load(
//                            this, "Sending request", "Loading user information, please wait..."
//                        )
//
//                        loadDialog.show()
//                        CoroutineScope(Dispatchers.IO).launch {
//                            val pref = getSharedPreferences(preferencesName, MODE_PRIVATE)
//                            val username = pref.getString("ApiUsername", "").toString()
//
//                            val response = viewModel.getUser(username)
//                            if (response.isSuccessful) {
//                                val user = response.body()
//
//                                if (user != null) {
//                                    CoroutineScope(Dispatchers.Main).launch {
//                                        val intent = Intent(
//                                            this@MainActivity, ManageProfileActivity::class.java
//                                        )
//
//                                        intent.putExtra("user", user)
//                                        startActivity(intent)
//                                    }
//                                }
//                            } else {
//                                CoroutineScope(Dispatchers.Main).launch {
//                                    Dialogs.confirm(
//                                        this@MainActivity,
//                                        R.drawable.ic_error,
//                                        "Loading information failed",
//                                        "Failed to load the user information."
//                                    )
//                                }
//                            }
//
//                            loadDialog.dismiss()
//                        }
//                    } else {
//                        startActivity(Intent(this, LoginActivity::class.java))
//                    }
//                } catch (ex: Exception) {
//                    Dialogs.exception(this, ex);
//                }
            }

            R.id.menuSettings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }

            R.id.menuAboutUs -> {
                startActivity(Intent(this, AboutUsActivity::class.java))
            }
        }

        b.main.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setupSearchBar() {
        b.txtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                loadDatabaseTable(s.toString())
            }
        })
    }

    private fun setupNavigationHeader() {
        val view = b.navMain.getHeaderView(0)
        val pref = getSharedPreferences(preferencesName, MODE_PRIVATE)

        val imgLogo = view.findViewById<ImageView>(R.id.imgLogo)
        val imgTheme = view.findViewById<ImageView>(R.id.imgTheme)

        val animator = ObjectAnimator.ofFloat(imgLogo, View.ROTATION, 0f, 360f)

        animator.duration = 5000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.RESTART
        animator.interpolator = LinearInterpolator()

        animator.start()

        when (pref.getInt("Theme", 0)) {
            0 -> {
                imgTheme.setImageResource(R.drawable.ic_moon)
            }

            1 -> {
                imgTheme.setImageResource(R.drawable.ic_sun)
            }

            2 -> {
                if (isDarkTheme(this)) {
                    imgTheme.setImageResource(R.drawable.ic_sun)
                } else {
                    imgTheme.setImageResource(R.drawable.ic_moon)
                }
            }
        }

        imgTheme.setOnClickListener {
            if (isDarkTheme(this)) {
                changeTheme(0)
                imgTheme.setImageResource(R.drawable.ic_moon)

                pref.edit {
                    putInt("Theme", 0)
                }
            } else {
                changeTheme(1)
                imgTheme.setImageResource(R.drawable.ic_sun)

                pref.edit {
                    putInt("Theme", 1)
                }
            }
        }
    }

    private fun setupNavigationDrawer() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        b.navMain.setNavigationItemSelectedListener(this)
    }

    private fun changeBottomNavigationItem(view: LinearLayout) {
        val animation = AnimationUtils.loadAnimation(this, R.anim.pop_in)
        animation.duration = 300

        /* Get all the views in the bottom navigation view, but not itself */
        getAllViews(b.layNavigationBottom, false).forEach {
            /* Set the background color to transparent for all the views */
            it.setBackgroundColor(getColor(android.R.color.transparent))
            it.animation = null

            /* Reset all imageview color to iconColor */
            if (it is ShapeableImageView) {
                it.setColorFilter(
                    getResourceColor(R.color.foreground_less), PorterDuff.Mode.SRC_IN
                )
            } else if (it is TextView) {/* Reset all textview font style to normal and remove bold one */
                it.setTypeface(null, Typeface.NORMAL)
                it.setTextColor(getResourceColor(R.color.foreground_less))
            }
        }

        /* The view is linearlayout, so get all the views in the layout and set their properties */
        getAllViews(view, false).forEach {
            if (it is ShapeableImageView) {
                it.setColorFilter(getResourceColor(R.color.theme), PorterDuff.Mode.SRC_IN)
                it.startAnimation(animation)
            } else if (it is TextView) {
                it.setTypeface(null, Typeface.BOLD)
                it.setTextColor(getResourceColor(R.color.theme))
            }
        }
    }

    private fun loadDatabaseTable(searchQuery: String = "") {
        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_down)
        animation.duration = 300

        when (currentIndex) {
            1 -> {
                b.tvAppName.text = "Accounts"
                loadAccounts(searchQuery)
            }

            2 -> {
                b.tvAppName.text = "Bank Cards"
                loadBankCards(searchQuery)
            }

            3 -> {
                b.tvAppName.text = "Contacts"
                loadContacts(searchQuery)
            }

            4 -> {
                b.tvAppName.text = "Notes"
                loadNotes(searchQuery)
            }
        }

        b.tvAppName.animation = animation
    }

    private fun setupViewModel() {/* used indexing instead of get method (get(ViewModel::class.java)) */
        databaseViewModel = ViewModelProvider(this)[DatabaseViewModel::class.java]
    }

    private fun loadAccounts(searchQuery: String = "") {
        accountAdapter = AccountAdapter(this)
        disableSelectionMode()

        b.rcMain.adapter = accountAdapter
        b.rcMain.layoutManager = LinearLayoutManager(this)

        if (searchQuery.isEmpty()) {
            databaseViewModel.allAccounts.observe(this) { accounts ->
                accountAdapter.accounts = accounts
                showEmptyList(accounts.isEmpty())
            }
        } else {
            databaseViewModel.setAccountSearchQuery(searchQuery)
            databaseViewModel.accountSearchResults.observe(this) { accounts ->
                accountAdapter.accounts = accounts
                showEmptyList(accounts.isEmpty())
            }
        }

        accountAdapter.setOnItemClickListener(object : AccountAdapter.OnItemClickListener {
            override fun onItemClick(
                checkBox: MaterialCheckBox, account: Account
            ) {
                if (isSelectionModeActivated) {
                    if (removeSelectedItem(checkBox, account)) {
                        checkBox.isChecked = false
                    } else {
                        addNewSelectedItem(checkBox, account)
                        checkBox.isChecked = true
                    }

                    if (selectedItems.isEmpty()) {
                        disableSelectionMode()
                    }
                } else {
                    val accountDetailsDialog =
                        BottomDialogAccountDetails(this@MainActivity, account)
                    accountDetailsDialog.show(supportFragmentManager, "Account Details")
                }
            }

            override fun onItemLongClick(checkBox: MaterialCheckBox, account: Account) {
                if (!isSelectionModeActivated) {
                    enableSelectionMode()

                    addNewSelectedItem(checkBox, account)
                    checkBox.isChecked = true
                }
            }
        })
    }

    private fun loadBankCards(searchQuery: String = "") {
        bankCardAdapter = BankCardAdapter(this)
        disableSelectionMode()

        b.rcMain.adapter = bankCardAdapter
        b.rcMain.layoutManager = LinearLayoutManager(this)

        if (searchQuery.isEmpty()) {
            databaseViewModel.allBankCards.observe(this) { bankCards ->
                bankCardAdapter.bankCards = bankCards
                showEmptyList(bankCards.isEmpty())
            }
        } else {
            databaseViewModel.setBankCardSearchQuery(searchQuery)
            databaseViewModel.bankCardSearchResults.observe(this) { bankCards ->
                bankCardAdapter.bankCards = bankCards
                showEmptyList(bankCards.isEmpty())
            }
        }

        bankCardAdapter.setOnItemClickListener(object : BankCardAdapter.OnItemClickListener {
            override fun onItemClick(
                checkBox: MaterialCheckBox, bankCard: BankCard
            ) {
                if (isSelectionModeActivated) {
                    if (removeSelectedItem(checkBox, bankCard)) {
                        checkBox.isChecked = false
                    } else {
                        addNewSelectedItem(checkBox, bankCard)
                        checkBox.isChecked = true
                    }

                    if (selectedItems.isEmpty()) {
                        disableSelectionMode()
                    }
                } else {
                    val bankCardDetailsDialog =
                        BottomDialogBankCardDetails(this@MainActivity, bankCard)
                    bankCardDetailsDialog.show(supportFragmentManager, "Bank Card Details")
                }
            }

            override fun onItemLongClick(checkBox: MaterialCheckBox, bankCard: BankCard) {
                if (!isSelectionModeActivated) {
                    enableSelectionMode()

                    addNewSelectedItem(checkBox, bankCard)
                    checkBox.isChecked = true
                }
            }
        })
    }

    private fun loadContacts(searchQuery: String = "") {
        contactAdapter = ContactAdapter(this)
        disableSelectionMode()

        b.rcMain.adapter = contactAdapter
        b.rcMain.layoutManager = LinearLayoutManager(this)

        if (searchQuery.isEmpty()) {
            databaseViewModel.allContacts.observe(this) { contacts ->
                contactAdapter.contacts = contacts
                showEmptyList(contacts.isEmpty())
            }
        } else {
            databaseViewModel.setContactSearchQuery(searchQuery)
            databaseViewModel.contactSearchResults.observe(this) { contacts ->
                contactAdapter.contacts = contacts
                showEmptyList(contacts.isEmpty())
            }
        }

        contactAdapter.setOnItemClickListener(object : ContactAdapter.OnItemClickListener {
            override fun onItemClick(
                checkBox: MaterialCheckBox, contact: Contact
            ) {
                if (isSelectionModeActivated) {
                    if (removeSelectedItem(checkBox, contact)) {
                        checkBox.isChecked = false
                    } else {
                        addNewSelectedItem(checkBox, contact)
                        checkBox.isChecked = true
                    }

                    if (selectedItems.isEmpty()) {
                        disableSelectionMode()
                    }
                } else {
                    val contactDetailsDialog =
                        BottomDialogContactDetails(this@MainActivity, contact)
                    contactDetailsDialog.show(supportFragmentManager, "Contact Details")
                }
            }

            override fun onItemLongClick(checkBox: MaterialCheckBox, contact: Contact) {
                if (!isSelectionModeActivated) {
                    enableSelectionMode()

                    addNewSelectedItem(checkBox, contact)
                    checkBox.isChecked = true
                }
            }
        })
    }

    private fun loadNotes(searchQuery: String = "") {
        noteAdapter = NoteAdapter(this)
        disableSelectionMode()

        b.rcMain.adapter = noteAdapter
        b.rcMain.layoutManager = LinearLayoutManager(this)

        if (searchQuery.isEmpty()) {
            databaseViewModel.allNotes.observe(this) { notes ->
                noteAdapter.notes = notes
                showEmptyList(notes.isEmpty())
            }
        } else {
            databaseViewModel.setNoteSearchQuery(searchQuery)
            databaseViewModel.noteSearchResults.observe(this) { notes ->
                noteAdapter.notes = notes
                showEmptyList(notes.isEmpty())
            }
        }

        noteAdapter.setOnItemClickListener(object : NoteAdapter.OnItemClickListener {
            override fun onItemClick(
                checkBox: MaterialCheckBox, note: Note
            ) {
                if (isSelectionModeActivated) {
                    if (removeSelectedItem(checkBox, note)) {
                        checkBox.isChecked = false
                    } else {
                        addNewSelectedItem(checkBox, note)
                        checkBox.isChecked = true
                    }

                    if (selectedItems.isEmpty()) {
                        disableSelectionMode()
                    }
                } else {
                    val noteDetailsDialog = BottomDialogNoteDetails(this@MainActivity, note)
                    noteDetailsDialog.show(supportFragmentManager, "Note Details")
                }
            }

            override fun onItemLongClick(checkBox: MaterialCheckBox, note: Note) {
                if (!isSelectionModeActivated) {
                    enableSelectionMode()

                    addNewSelectedItem(checkBox, note)
                    checkBox.isChecked = true
                }
            }
        })
    }

    private fun setOnBackPressedListener() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (b.main.isDrawerOpen(GravityCompat.START)) {
                    b.main.closeDrawer(GravityCompat.START)
                } else {
                    if (isSelectionModeActivated) {
                        disableSelectionMode()
                    } else {
                        val currentTime = System.currentTimeMillis()

                        /* if user press back button again under 3 seconds */
                        if (currentTime - backPressedTime < 3000) {
                            finish()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Press back button again to logout",
                                Toast.LENGTH_SHORT
                            ).show()

                            backPressedTime = currentTime
                        }
                    }
                }
            }
        }

        onBackPressedDispatcher.addCallback(
            this, onBackPressedCallback
        )
    }

    private fun enableSelectionMode() {
        isSelectionModeActivated = true
        showOptionsButtons(true)

        when (currentIndex) {
            1 -> {
                accountAdapter.setShowCheckboxes(true)
            }

            2 -> {
                bankCardAdapter.setShowCheckboxes(true)
            }

            3 -> {
                contactAdapter.setShowCheckboxes(true)
            }

            4 -> {
                noteAdapter.setShowCheckboxes(true)
            }
        }
    }

    private fun disableSelectionMode() {
        isSelectionModeActivated = false
        showOptionsButtons(false)

        when (currentIndex) {
            1 -> {
                accountAdapter.setShowCheckboxes(false)
            }

            2 -> {
                bankCardAdapter.setShowCheckboxes(false)
            }

            3 -> {
                contactAdapter.setShowCheckboxes(false)
            }

            4 -> {
                noteAdapter.setShowCheckboxes(false)
            }
        }

        selectedItems.clear()
        for (item in selectedViews) {
            item.isChecked = false
        }

        selectedViews.clear()
        updateSelectedItemsText()
    }

    /**
     * we don't want duplicate items in the list, so first check the list
     */
    private fun addNewSelectedItem(checkBox: MaterialCheckBox, item: Any) {
        selectedItems.add(item)
        selectedViews.add(checkBox)

        updateSelectedItemsText()
    }

    /**
     * removes the selected item, if item exist in the list return true else returns false
     */
    private fun removeSelectedItem(checkBox: MaterialCheckBox, item: Any): Boolean {
        if (selectedItems.contains(item)) {
            selectedItems.remove(item)
            selectedViews.remove(checkBox)

            updateSelectedItemsText()
            return true
        }

        return false
    }

    /* this function help to reduce code to access resource color */
    private fun getResourceColor(colorResource: Int): Int {
        return ContextCompat.getColor(this, colorResource)
    }

    private fun showEmptyList(visible: Boolean) {
        val animation = AnimationUtils.loadAnimation(this, R.anim.bounce)
        animation.duration = 500

        if (visible) {
            b.lottieEmpty.visibility = View.VISIBLE
            b.tvEmpty.visibility = View.VISIBLE
            b.tvAdd.visibility = View.VISIBLE

            b.lottieEmpty.animation = animation
            b.tvEmpty.animation = animation
            b.tvAdd.animation = animation
        } else {
            b.lottieEmpty.animation = null
            b.tvEmpty.animation = null
            b.tvAdd.animation = null

            b.lottieEmpty.visibility = View.GONE
            b.tvEmpty.visibility = View.GONE
            b.tvAdd.visibility = View.GONE
        }
    }

    private fun showOptionsButtons(visible: Boolean) {
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        fadeInAnimation.duration = 300
        fadeOutAnimation.duration = 300

        if (visible) {
            b.btnDelete.apply {
                visibility = View.VISIBLE
                animation = fadeInAnimation
                isClickable = true
            }

            b.btnShare.apply {
                visibility = View.VISIBLE
                animation = fadeInAnimation
                isClickable = true
            }

            b.btnCopy.apply {
                visibility = View.VISIBLE
                animation = fadeInAnimation
                isClickable = true
            }
        } else {
            b.btnDelete.apply {
                visibility = View.GONE
                animation = fadeOutAnimation
                isClickable = false
            }

            b.btnShare.apply {
                visibility = View.GONE
                animation = fadeOutAnimation
                isClickable = false
            }

            b.btnCopy.apply {
                visibility = View.GONE
                animation = fadeOutAnimation
                isClickable = false
            }
        }
    }

    private fun updateSelectedItemsText() {
        if (selectedItems.isEmpty()) {
            when (currentIndex) {
                1 -> {
                    b.tvAppName.text = "Accounts"
                }

                2 -> {
                    b.tvAppName.text = "Bank Cards"
                }

                3 -> {
                    b.tvAppName.text = "Contacts"
                }

                4 -> {
                    b.tvAppName.text = "Notes"
                }
            }
        } else {
            b.tvAppName.text = "${selectedItems.size} Selected"
        }
    }
}