import {
    addListToDropdown,
    Alignment,
    Base64UploadAdapter,
    BlockQuote,
    Bold,
    ClassicEditor,
    Collection,
    Command,
    createDropdown,
    createLabeledInputText,
    Dialog,
    Essentials,
    Font,
    Heading,
    HorizontalLine,
    Image,
    ImageCaption,
    ImageInsert,
    ImageResize,
    ImageStyle,
    ImageToolbar,
    Indent,
    Italic,
    LabeledFieldView,
    Link,
    List,
    Paragraph,
    Plugin,
    RemoveFormat,
    SourceEditing,
    SpecialCharacters,
    SpecialCharactersEssentials,
    Strikethrough,
    Table,
    TableToolbar,
    TableProperties,
    toWidget,
    Underline,
    Undo,
    View,
    Widget
} from 'ckeditor5';

// ===== PLACEHOLDER CONFIGURATION =====
// DO NOT CHANGE, (ADD is ok) THESE ARE VALUE WITHIN THE TEMPLATES, CHANGING MEANS UPDATING THE TEMPLATES
const PLACEHOLDERS = [
    {
        group: 'Klant',
        items: [
            {label: 'aanhef', value: 'data.customer.salutation'},
            {label: 'klantnaam', value: 'data.customer.customerName'},
            {label: 'adres', value: 'data.customer.address'},
            {label: 'Email', value: 'data.customer.email'},
            {label: 'telefoon', value: 'data.customer.phone'}
        ]
    },
    {
        group: 'Dier',
        items: [
            {label: 'diernaam', value: 'data.pet.name'},
            {label: 'soort', value: 'data.pet.species'},
            {label: 'sex', value: 'data.pet.sex'},
            {label: 'leeftijd', value: 'data.pet.age'}
        ]
    },
    {
        group: 'Herinnering',
        items: [
            {label: 'datum', value: 'data.reminder.dueDate'},
            {label: 'herinnering', value: 'data.reminder.reminderText'}
        ]
    },
    {
        group: 'localmember',
        items: [
            {label: 'name', value: 'data.localmember.name'}
        ]
    },
    {
        group: 'Label',
        items: [
            {label: 'exipireDate', value: 'data.expireDate'},
            {label: 'ownerName', value: 'data.ownerName'},
            {label: 'petName', value: 'data.petName'},
            {label: 'prescription', value: 'data.prescription'},
            {label: 'explenation', value: 'data.explenation'},
            {label: 'vet', value: 'data.vet'},
            {label: 'expiryDate', value: 'data.expiryDate'},
        ]
    }
];

// ===== PLACEHOLDER COMMAND =====
class PlaceholderCommand extends Command {
    execute({value, label}) {
        const editor = this.editor;
        editor.model.change(writer => {
            const element = writer.createElement('placeholder', {value, label});
            editor.model.insertContent(element);
        });
    }
}

// ===== PLACEHOLDER PLUGIN =====
class PlaceholderPlugin extends Plugin {
    static get requires() {
        return [Widget];
    }

    init() {
        const editor = this.editor;

        // Schema
        editor.model.schema.register('placeholder', {
            allowWhere: '$text',
            isInline: true,
            isObject: true,
            allowAttributes: ['value', 'label']
        });

        // EDITING DOWNCAST - how it appears in the editor
        editor.conversion.for('editingDowncast').elementToElement({
            model: 'placeholder',
            view: (modelItem, {writer}) => {
                const span = writer.createContainerElement('span', {
                    class: 'placeholder-token',
                    'data-placeholder': modelItem.getAttribute('value')
                });
                writer.insert(writer.createPositionAt(span, 0), writer.createText(modelItem.getAttribute('label')));
                return toWidget(span, writer);
            }
        });

        // DATA DOWNCAST - what gets saved (thymeleaf format)
        editor.conversion.for('dataDowncast').elementToElement({
            model: 'placeholder',
            view: (modelItem, {writer}) => {
                const value = modelItem.getAttribute('value');
                // Create a text element that will be stringified as plain text
                return writer.createRawElement('span', {'data-thymeleaf': 'true'}, function (domElement) {
                    domElement.textContent = '[[${' + value + '}]]'; // [[${reminder.reminderText}]]
                });
            }
        });

        // Upcast
        editor.conversion.for('upcast').elementToElement({
            view: {
                name: 'span',
                attributes: {'data-placeholder': true}
            },
            model: (viewElement, {writer}) => {
                return writer.createElement('placeholder', {
                    value: viewElement.getAttribute('data-placeholder'),
                    label: viewElement.getChild(0)?.data || ''
                });
            }
        });

        // Upcast - reading from DB and change to  showing in HTML as placeholder
        editor.conversion.for('upcast').elementToElement({
            view: {
                name: 'span',
                attributes: {
                    'data-thymeleaf': 'true'
                }
            },
            model: (viewElement, {writer}) => {
                const text = viewElement.getChild(0)?.data || '';
                // Extract value from {{customer.salutation}} format
                // const match = text.match(/\{\{([^}]+)\}\}/); // mustache``````````````````
                const match =  text.match(/\[\[\$\{([^}]+)\}\]\]/); // thymeleaf
                const value = match ? match[1] : text;

                let label = value.split('.').pop(); // fallback
                for (const group of PLACEHOLDERS) {
                    const item = group.items.find(i => i.value === value);
                    if (item) {
                        label = item.label;
                        break;
                    }
                }

                return writer.createElement('placeholder', {
                    value: value,
                    label: label
                });
            }
        });

        // Register command
        editor.commands.add('insertPlaceholder', new PlaceholderCommand(editor));

        // UI
        editor.ui.componentFactory.add('placeholders', locale => {
            const dropdown = createDropdown(locale);
            dropdown.buttonView.set({
                label: 'Placeholder',
                withText: true,
                tooltip: 'Insert Placeholder'
            });

            const items = new Collection();
            PLACEHOLDERS.forEach(group => {
                group.items.forEach(item => {
                    items.add({
                        type: 'button',
                        model: {
                            label: `${group.group} ${item.label}`,
                            withText: true,
                            value: item.value,
                            itemLabel: item.label
                        }
                    });
                });
            });

            addListToDropdown(dropdown, items);

            dropdown.on('execute', evt => {
                editor.execute('insertPlaceholder', {
                    value: evt.source.value,
                    label: evt.source.itemLabel
                });
            });

            return dropdown;
        });
    }
}


// ===== BUTTON CONFIGURATION =====
const BUTTON_STYLES = [
    {name: 'Green', color: '#4CAF50', textColor: '#ffffff'},
    {name: 'Blue', color: '#2196F3', textColor: '#ffffff'},
    {name: 'Orange', color: '#ff9800', textColor: '#ffffff'},
    {name: 'Red', color: '#f44336', textColor: '#ffffff'}
];

// ===== BUTTON COMMAND =====
class ButtonCommand extends Command {
    execute({text, url, style}) {
        const editor = this.editor;
        editor.model.change(writer => {
            const element = writer.createElement('ctaButton', {text, url, style});
            editor.model.insertContent(element);
        });
    }
}

// ===== BUTTON PLUGIN =====
class ButtonPlugin extends Plugin {
    static get requires() {
        return [Widget, Dialog];
    }

    init() {
        const editor = this.editor;

        // Schema
        editor.model.schema.register('ctaButton', {
            allowWhere: '$block',
            isBlock: true,
            isObject: true,
            allowAttributes: ['text', 'url', 'style']
        });

        // EDITING DOWNCAST - visual in editor
        editor.conversion.for('editingDowncast').elementToElement({
            model: 'ctaButton',
            view: (modelItem, {writer}) => {
                const text = modelItem.getAttribute('text');
                const styleName = modelItem.getAttribute('style');
                const style = BUTTON_STYLES.find(s => s.name === styleName) || BUTTON_STYLES[0];

                const button = writer.createContainerElement('div', {
                    class: 'cta-button-widget',
                    'data-button-text': text,
                    'data-button-url': modelItem.getAttribute('url'),
                    'data-button-style': styleName,
                    style: `text-align: center; padding: 20px 0;`
                });

                const buttonInner = writer.createContainerElement('span', {
                    style: `display: inline-block; background-color: ${style.color}; color: ${style.textColor}; padding: 12px 24px; border-radius: 4px; font-weight: bold;`
                });

                writer.insert(writer.createPositionAt(buttonInner, 0), writer.createText(text));
                writer.insert(writer.createPositionAt(button, 0), buttonInner);

                return toWidget(button, writer, {label: 'Button widget'});
            }
        });

        // DATA DOWNCAST - wrap table in a unique div
        editor.conversion.for('dataDowncast').elementToElement({
            model: 'ctaButton',
            view: (modelItem, {writer}) => {
                const text = modelItem.getAttribute('text');
                const url = modelItem.getAttribute('url');
                const styleName = modelItem.getAttribute('style');
                const style = BUTTON_STYLES.find(s => s.name === styleName) || BUTTON_STYLES[0];

                // Wrapper div with data attributes
                const wrapper = writer.createContainerElement('div', {
                    class: 'cta-button-email',
                    'data-cta-button': 'true',
                    'data-button-text': text,
                    'data-button-url': url,
                    'data-button-style': styleName
                });

                const table = writer.createContainerElement('table', {
                    role: 'presentation',
                    cellspacing: '0',
                    cellpadding: '0',
                    border: '0',
                    style: 'margin: 20px 0;'
                });

                const tr = writer.createContainerElement('tr');
                const td = writer.createContainerElement('td', {
                    style: `border-radius: 4px; background-color: ${style.color};`
                });

                const link = writer.createContainerElement('a', {
                    href: url,
                    style: `display: inline-block; padding: 12px 24px; font-size: 16px; color: ${style.textColor}; text-decoration: none; font-weight: bold;`
                });

                writer.insert(writer.createPositionAt(link, 0), writer.createText(text));
                writer.insert(writer.createPositionAt(td, 0), link);
                writer.insert(writer.createPositionAt(tr, 0), td);
                writer.insert(writer.createPositionAt(table, 0), tr);
                writer.insert(writer.createPositionAt(wrapper, 0), table);

                return wrapper;
            }
        });

// UPCAST - look for wrapper div
        editor.conversion.for('upcast').elementToElement({
            view: {
                name: 'div',
                classes: 'cta-button-email'
            },
            model: (viewElement, {writer}) => {
                return writer.createElement('ctaButton', {
                    text: viewElement.getAttribute('data-button-text') || 'Button',
                    url: viewElement.getAttribute('data-button-url') || '#',
                    style: viewElement.getAttribute('data-button-style') || 'Green'
                });
            },
            converterPriority: 'high'
        });
        // Register command
        editor.commands.add('insertButton', new ButtonCommand(editor));

        // UI
        editor.ui.componentFactory.add('ctaButton', locale => {
            const dropdown = createDropdown(locale);
            dropdown.buttonView.set({
                label: '🔘 Button',
                withText: true,
                tooltip: 'Insert button'
            });

            const items = new Collection();
            BUTTON_STYLES.forEach(style => {
                items.add({
                    type: 'button',
                    model: {
                        label: `${style.name} Button`,
                        withText: true,
                        styleName: style.name
                    }
                });
            });

            addListToDropdown(dropdown, items);

            this.listenTo(dropdown, 'execute', evt => {
                const styleName = evt.source.styleName;
                this._showButtonDialog(editor, styleName);
            });

            return dropdown;
        });
    }

    _showButtonDialog(editor, styleName) {
        const dialog = editor.plugins.get('Dialog');
        const formView = this._createFormView(editor.locale);

        dialog.show({
            id: 'buttonDialog',
            title: 'Insert Button',
            content: formView,
            actionButtons: [
                {
                    label: 'Cancel',
                    withText: true,
                    onExecute: () => dialog.hide()
                },
                {
                    label: 'Insert',
                    class: 'ck-button-action',
                    withText: true,
                    onExecute: () => {
                        const text = formView.textInput.fieldView.element.value;
                        const url = formView.urlInput.fieldView.element.value;

                        if (text && url) {
                            editor.execute('insertButton', {
                                text: text,
                                url: url,
                                style: styleName
                            });
                        }
                        dialog.hide();
                        editor.editing.view.focus();
                    }
                }
            ]
        });
    }

    _createFormView(locale) {
        const view = new View(locale);

        const textInput = new LabeledFieldView(locale, createLabeledInputText);
        textInput.label = 'Button Text';
        textInput.fieldView.placeholder = 'Button name';
        textInput.fieldView.value = '';

        const urlInput = new LabeledFieldView(locale, createLabeledInputText);
        urlInput.label = 'Button URL';
        urlInput.fieldView.placeholder = 'Url';
        urlInput.fieldView.value = '';

        view.setTemplate({
            tag: 'form',
            attributes: {
                class: ['ck', 'ck-button-form'],
                tabindex: '-1'
            },
            children: [textInput, urlInput]
        });

        view.textInput = textInput;
        view.urlInput = urlInput;

        return view;
    }
}


// ===== EDITOR INITIALIZATION =====
function initWhenReady() {
    const editorElement = document.querySelector('#editor');
    if (!editorElement) {
        setTimeout(initWhenReady, 50);
        return;
    }

    ClassicEditor.create(editorElement, {
        allowedContent: true,
        plugins: [
            Essentials, Bold, Italic, Underline, Strikethrough, Font, Paragraph, Heading, List, Link, BlockQuote, Table, TableToolbar,TableProperties,
            Alignment, Indent, HorizontalLine, SpecialCharacters, SpecialCharactersEssentials, RemoveFormat, Undo, SourceEditing,
            PlaceholderPlugin, ButtonPlugin,
            Image, ImageToolbar, ImageCaption, ImageStyle, ImageResize, ImageInsert, Base64UploadAdapter
        ],
        toolbar: {
            items: [
                'placeholders', 'ctaButton', '|', 'heading', '|', 'fontSize', 'fontFamily', 'fontColor', 'fontBackgroundColor', '|', 'bold', 'italic', 'underline', 'strikethrough', '|',
                'alignment', '|', 'numberedList', 'bulletedList', '|', 'outdent', 'indent', '|', 'link', 'insertImage',
                'insertTable', 'tableProperties',
                'blockQuote', '|', 'horizontalLine', 'specialCharacters', '|',
                'removeFormat', '|', 'undo', 'redo', '|', 'sourceEditing'
            ],
            shouldNotGroupWhenFull: true
        },
        image: {
            toolbar: [
                'imageStyle:inline',
                'imageStyle:block',
                'imageStyle:side',
                '|',
                'toggleImageCaption',
                'imageTextAlternative'
            ]
        },
        table: {
            contentToolbar: ['tableColumn', 'tableRow', 'mergeTableCells']
        }
    })
        .then(editor => {
            window.editor = editor;
            console.log('✅ CKEditor ready');
        })
        .catch(error => {
            console.error('CKEditor error:', error);
        });
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initWhenReady);
} else {
    initWhenReady();
}