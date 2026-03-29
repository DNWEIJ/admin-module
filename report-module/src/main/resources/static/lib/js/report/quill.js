// ─── Register Blots ───────────────────────────────────────────────────────────
function registerBlots() {
    const Embed = Quill.import('blots/embed');

    class PlaceholderBlot extends Embed {
        static create(value) {
            const node = super.create();
            node.setAttribute('data-key', value.key || value);
            node.classList.add('placeholder');
            node.innerText = `{{${value.label || value.key}}}`;
            return node;
        }

        static value(node) {
            return {key: node.getAttribute('data-key'), label: node.innerText.replace(/{{|}}/g, '')};
        }
    }

    PlaceholderBlot.blotName = 'placeholder';
    PlaceholderBlot.tagName = 'span';
    Quill.register(PlaceholderBlot);

    const BlotFormatter = QuillBlotFormatter2.default;
    Quill.register('modules/blotFormatter2', BlotFormatter);

    const BlockEmbed = Quill.import('blots/block/embed');

    class DividerBlot extends BlockEmbed {
    }

    DividerBlot.blotName = 'divider';
    DividerBlot.tagName = 'hr';
    Quill.register(DividerBlot);
}

// ─── NO Shadow DOM - Standard DOM Setup ───────────────────────────────────────

function createEditor(hostElementId) {
    const host = document.getElementById(hostElementId);

    // Clear existing content
    host.innerHTML = '';

    // Quill CSS
    const quillStyle = document.createElement('link');
    quillStyle.rel = 'stylesheet';
    quillStyle.href = 'https://cdn.jsdelivr.net/npm/quill@2/dist/quill.snow.css';
    document.head.appendChild(quillStyle);

    // Blot-formatter CSS
    const blotFormatterStyle = document.createElement('link');
    blotFormatterStyle.rel = 'stylesheet';
    blotFormatterStyle.href = 'https://cdn.jsdelivr.net/npm/@enzedonline/quill-blot-formatter2/dist/css/quill-blot-formatter2.css';
    document.head.appendChild(blotFormatterStyle);

    // Toolbar
    const toolbarDiv = document.createElement('div');
    toolbarDiv.id = 'quill-toolbar';
    toolbarDiv.innerHTML = `
    <span class="ql-formats">
        <select class="ql-font">
            <option selected></option>
            <option value="serif"></option>
            <option value="monospace"></option>
        </select>
    </span>
    <span class="ql-formats">
        <select class="ql-header">
            <option value="1"></option>
            <option value="2"></option>
            <option value="3"></option>
            <option value="4"></option>
            <option value="5"></option>
            <option value="6"></option>
            <option selected></option>
        </select>
    </span>    
    <span class="ql-formats">
        <button class="ql-bold"></button>
        <button class="ql-italic"></button>
        <button class="ql-underline"></button>
        <button class="ql-strike"></button>
    </span>

   
    <span class="ql-formats">
        <button class="ql-script" value="sub"></button>
        <button class="ql-script" value="super"></button>
    </span>

    <span class="ql-formats">
        <button class="ql-blockquote"></button>
        <button class="ql-code-block"></button>
    </span>
    <span class="ql-formats">
        <button class="ql-list" value="ordered"></button>
        <button class="ql-list" value="bullet"></button>
        <button class="ql-list" value="check"></button>
    </span>
    <span class="ql-formats">
        <button class="ql-indent" value="-1"></button>
        <button class="ql-indent" value="+1"></button>
    </span>
    <span class="ql-formats">
        <select class="ql-align">
            <option selected></option>
            <option value="center"></option>
            <option value="right"></option>
            <option value="justify"></option>
        </select>
    </span>
    <span class="ql-formats">
        <button class="ql-direction" value="rtl"></button>
    </span>
    <span class="ql-formats">
        <button class="ql-link"></button>
        <button class="ql-image"></button>
    </span>
    <span class="ql-formats">
        <button class="ql-clean"></button>
    </span>
<span class="ql-formats">
    <button type="button" id="view-html-btn" title="View HTML Source">
        &lt;/&gt;
    </button>
</span>
    
    <span class="ql-formats">
        <div class="placeholder-wrapper" style="position:relative;display:inline-block;vertical-align:middle;">
            <button id="placeholderTrigger" class="ql-placeholder-trigger" title="Insert Placeholder">{placeholder}</button>
            <ul id="placeholderDropdown">
                <li class="placeholder-group">Customer
                    <ul>
                        <li data-key="customer.salutation" data-label="Customer Name">customer_salutation</li>
                        <li data-key="customer.address" data-label="Customer Address">customer_address</li>
                        <li data-key="customer.balance" data-label="Customer Address">customer_balance</li>
                    </ul>
                </li>
                <li class="placeholder-group">Pet
                    <ul>
                        <li data-key="pet.name" data-label="Pet Name">pet_name</li>
                        <li data-key="pet.sex" data-label="Pet Type">pet_sex</li>
                        <li data-key="pet.age" data-label="Pet Type">pet_age</li>
                    </ul>
                </li>
                <li class="placeholder-group">Reminder
                    <ul>
                        <li data-key="reminder.date" data-label="Reminder date">reminder_date</li>
                        <li data-key="reminder.reminderText" data-label="Reminder text">reminder_purpose</li>
                    </ul>
                </li>                    
            </ul>
        </div>
    </span>
    `;
    host.appendChild(toolbarDiv);

    // Editor
    const editorDiv = document.createElement('div');
    editorDiv.id = 'quill-editor';
    host.appendChild(editorDiv);

    // Styles
    const style = document.createElement('style');
    style.textContent = `
        #quill-editor {
            height: 400px;
            position: relative;            
        }

        #placeholderDropdown {
            display: none;
            position: absolute;
            top: 100%;
            left: 0;
            z-index: 1000;
            list-style: none;
            margin: 2px 0 0;
            padding: 4px 0;
            border: 1px solid #ccc;
            border-radius: 4px;
            background: #fff;
            font-size: 0.875rem;
            min-width: 200px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }

        #placeholderDropdown.open {
            display: block;
        }

        #placeholderDropdown > li.placeholder-group {
            padding: 4px 8px 2px;
            font-size: 0.75rem;
            font-weight: 600;
            color: #888;
            text-transform: uppercase;
            letter-spacing: 0.05em;
            cursor: default;
        }

        #placeholderDropdown ul {
            list-style: none;
            margin: 0;
            padding: 0;
        }

        #placeholderDropdown li[data-key] {
            padding: 5px 16px;
            cursor: pointer;
            color: #333;
        }

        #placeholderDropdown li[data-key]:hover {
            background: #06c;
            color: #fff;
        }

        .ql-placeholder-trigger {
            font-size: 13px !important;
            font-weight: bold !important;
            width: auto !important;
            padding: 0 6px !important;
        }
    `;
    document.head.appendChild(style);

    return host;
}

// ─── Quill Initialization ─────────────────────────────────────────────────────

function initQuill(container, initialContent) {
    const quill = new Quill(container.querySelector('#quill-editor'), {
        modules: {
            blotFormatter2: {
                video: {
                    registerCustomVideoBlot: true
                },
                resize: {
                    useRelativeSize: true,
                    allowResizeModeChange: true,
                    imageOversizeProtection: true
                },
                image: {
                    registerImageTitleBlot: true,
                    allowCompressor: true,
                    compressorOptions: {
                        jpegQuality: 0.7,
                        maxWidth: 1000
                    }
                },
                containTooltipPosition: true
            },
            toolbar: {
                container: container.querySelector('#quill-toolbar'),
                handlers: {
                    hr: function () {
                        const range = this.quill.getSelection(true);
                        this.quill.insertEmbed(range.index, 'divider', true, Quill.sources.USER);
                        this.quill.setSelection(range.index + 1, Quill.sources.SILENT);
                    }
                }
            }
        },
        theme: 'snow'
    });

    if (initialContent) {
        quill.root.innerHTML = initialContent;
    }


    // Add HTML source toggle button
    const toolbar = container.querySelector('#quill-toolbar');
    toolbar.insertAdjacentHTML('beforeend', `
        <span class="ql-formats">
            <button type="button" id="toggle-html-btn" title="Toggle HTML Source" style="width: auto !important; padding: 3px 8px !important;">
                &lt;HTML&gt;
            </button>
        </span>
    `);

    // Create HTML source textarea AFTER the editor
    const editorContainer = container.querySelector('#quill-editor');
    editorContainer.insertAdjacentHTML('afterend', `
        <textarea id="html-source" style="display: none; width: 100%; height: 400px; font-family: monospace; padding: 10px; border: 1px solid #ccc;"></textarea>
    `);

    // Toggle functionality
    let isHtmlMode = false;
    const toggleBtn = container.querySelector('#toggle-html-btn');
    const htmlTextarea = container.querySelector('#html-source');

    toggleBtn.addEventListener('click', function(e) {
        e.preventDefault();
        e.stopPropagation();

        if (isHtmlMode) {
            // Switch back to visual editor
            quill.root.innerHTML = htmlTextarea.value;
            editorContainer.style.display = 'block';
            htmlTextarea.style.display = 'none';
            toggleBtn.textContent = '<HTML>';
            isHtmlMode = false;
        } else {
            // Switch to HTML source view
            htmlTextarea.value = quill.root.innerHTML;
            editorContainer.style.display = 'none';
            htmlTextarea.style.display = 'block';
            toggleBtn.textContent = 'Editor';
            isHtmlMode = true;
        }
    });



    return quill;
}

// ─── Placeholder Dropdown ─────────────────────────────────────────────────────

function initPlaceholderDropdown(quill, container) {
    let savedRange = null;

    quill.on('selection-change', (range) => {
        if (range) savedRange = range;
    });

    const trigger = container.querySelector('#placeholderTrigger');
    const dropdown = container.querySelector('#placeholderDropdown');
    const placeholderWrapper = container.querySelector('.placeholder-wrapper');

    // Toggle on trigger click
    trigger.addEventListener('click', (e) => {
        e.stopPropagation();
        savedRange = quill.getSelection() || savedRange;
        dropdown.classList.toggle('open');
    });

    // Close when clicking outside wrapper
    document.addEventListener('click', (e) => {
        if (dropdown.classList.contains('open') && !placeholderWrapper.contains(e.target)) {
            dropdown.classList.remove('open');
        }
    });

    // Handle placeholder selection
    dropdown.addEventListener('click', (e) => {
        const li = e.target.closest('li[data-key]');
        if (!li) return;

        dropdown.classList.remove('open');

        const key = li.dataset.key;
        const label = li.dataset.label || key;
        const range = savedRange || {index: quill.getLength(), length: 0};

        quill.insertEmbed(range.index, 'placeholder', {key, label}, Quill.sources.USER);
        quill.insertText(range.index + 1, ' ', Quill.sources.USER);
        quill.setSelection(range.index + 2, 0, Quill.sources.SILENT);
        quill.focus();
    });
}

// ─── Form Sync ────────────────────────────────────────────────────────────────

function initFormSync(quill, hiddenInputId) {
    const form = document.querySelector('form');
    if (form) {
        form.addEventListener('submit', () => {
            const hiddenInput = document.getElementById(hiddenInputId);
            if (hiddenInput) {
                hiddenInput.value = quill.root.innerHTML;
            }
        });
    }
}

// ─── Right-click Remove Placeholder ──────────────────────────────────────────
function initContextMenu(quill) {
    quill.root.addEventListener('contextmenu', (e) => {
        const span = e.target.closest('.placeholder');
        if (span) {
            e.preventDefault();
            if (confirm('Remove this placeholder?')) span.remove();
        }
    });
}

// ─── Bootstrap ────────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', function init() {
    registerBlots();

    // Get initial content from the hidden input or from the editor div
    let initialContent = '';
    const contentInput = document.getElementById('contentToSave');
    const editorDiv = document.getElementById('editor');

    if (contentInput && contentInput.value) {
        initialContent = contentInput.value;
    } else if (editorDiv) {
        initialContent = editorDiv.innerHTML;
    }

    const container = createEditor('quill-wrapper');
    const quill = initQuill(container, initialContent);

    initPlaceholderDropdown(quill, container);
    initFormSync(quill, 'contentToSave');
    initContextMenu(quill);

    // Debug: Log when blot-formatter elements are created
    // console.log('Checking for blot-formatter elements in DOM...');
    // setTimeout(() => {
    //     const overlays = document.querySelectorAll('.blot-formatter__overlay');
    //     const toolbars = document.querySelectorAll('.blot-formatter__toolbar');
    //     console.log('Overlays found:', overlays.length);
    //     console.log('Toolbars found:', toolbars.length);
    //     overlays.forEach((el, i) => console.log(`Overlay ${i}:`, el, 'Parent:', el.parentElement));
    //     toolbars.forEach((el, i) => console.log(`Toolbar ${i}:`, el, 'Parent:', el.parentElement));
    // }, 1000);
});