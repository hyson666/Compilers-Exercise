/********************************************************************************
** Form generated from reading UI file 'lexical.ui'
**
** Created by: Qt User Interface Compiler version 5.8.0
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_LEXICAL_H
#define UI_LEXICAL_H

#include <QtCore/QVariant>
#include <QtWidgets/QAction>
#include <QtWidgets/QApplication>
#include <QtWidgets/QButtonGroup>
#include <QtWidgets/QGridLayout>
#include <QtWidgets/QHeaderView>
#include <QtWidgets/QLabel>
#include <QtWidgets/QMainWindow>
#include <QtWidgets/QMenuBar>
#include <QtWidgets/QPushButton>
#include <QtWidgets/QStatusBar>
#include <QtWidgets/QTextBrowser>
#include <QtWidgets/QTextEdit>
#include <QtWidgets/QToolBar>
#include <QtWidgets/QWidget>

QT_BEGIN_NAMESPACE

class Ui_Lexical
{
public:
    QWidget *centralWidget;
    QGridLayout *gridLayout;
    QPushButton *pushButton_4;
    QPushButton *pushButton_2;
    QPushButton *pushButton;
    QTextBrowser *textBrowser;
    QTextEdit *textEdit;
    QLabel *label;
    QPushButton *pushButton_3;
    QMenuBar *menuBar;
    QToolBar *mainToolBar;
    QStatusBar *statusBar;

    void setupUi(QMainWindow *Lexical)
    {
        if (Lexical->objectName().isEmpty())
            Lexical->setObjectName(QStringLiteral("Lexical"));
        Lexical->resize(587, 460);
        centralWidget = new QWidget(Lexical);
        centralWidget->setObjectName(QStringLiteral("centralWidget"));
        gridLayout = new QGridLayout(centralWidget);
        gridLayout->setSpacing(6);
        gridLayout->setContentsMargins(11, 11, 11, 11);
        gridLayout->setObjectName(QStringLiteral("gridLayout"));
        pushButton_4 = new QPushButton(centralWidget);
        pushButton_4->setObjectName(QStringLiteral("pushButton_4"));

        gridLayout->addWidget(pushButton_4, 3, 1, 1, 1);

        pushButton_2 = new QPushButton(centralWidget);
        pushButton_2->setObjectName(QStringLiteral("pushButton_2"));

        gridLayout->addWidget(pushButton_2, 1, 1, 1, 1);

        pushButton = new QPushButton(centralWidget);
        pushButton->setObjectName(QStringLiteral("pushButton"));

        gridLayout->addWidget(pushButton, 2, 1, 1, 1);

        textBrowser = new QTextBrowser(centralWidget);
        textBrowser->setObjectName(QStringLiteral("textBrowser"));

        gridLayout->addWidget(textBrowser, 6, 0, 1, 1);

        textEdit = new QTextEdit(centralWidget);
        textEdit->setObjectName(QStringLiteral("textEdit"));

        gridLayout->addWidget(textEdit, 0, 0, 5, 1);

        label = new QLabel(centralWidget);
        label->setObjectName(QStringLiteral("label"));

        gridLayout->addWidget(label, 5, 0, 1, 1);

        pushButton_3 = new QPushButton(centralWidget);
        pushButton_3->setObjectName(QStringLiteral("pushButton_3"));

        gridLayout->addWidget(pushButton_3, 0, 1, 1, 1);

        Lexical->setCentralWidget(centralWidget);
        menuBar = new QMenuBar(Lexical);
        menuBar->setObjectName(QStringLiteral("menuBar"));
        menuBar->setGeometry(QRect(0, 0, 587, 22));
        Lexical->setMenuBar(menuBar);
        mainToolBar = new QToolBar(Lexical);
        mainToolBar->setObjectName(QStringLiteral("mainToolBar"));
        Lexical->addToolBar(Qt::TopToolBarArea, mainToolBar);
        statusBar = new QStatusBar(Lexical);
        statusBar->setObjectName(QStringLiteral("statusBar"));
        Lexical->setStatusBar(statusBar);

        retranslateUi(Lexical);

        QMetaObject::connectSlotsByName(Lexical);
    } // setupUi

    void retranslateUi(QMainWindow *Lexical)
    {
        Lexical->setWindowTitle(QApplication::translate("Lexical", "Lexical", Q_NULLPTR));
        pushButton_4->setText(QApplication::translate("Lexical", "About", Q_NULLPTR));
        pushButton_2->setText(QApplication::translate("Lexical", "\346\211\223\345\274\200\346\226\207\344\273\266", Q_NULLPTR));
        pushButton->setText(QApplication::translate("Lexical", "\350\257\215\346\263\225\345\210\206\346\236\220", Q_NULLPTR));
        label->setText(QApplication::translate("Lexical", "\346\226\207\346\263\225\345\210\206\346\236\220\347\273\223\346\236\234\357\274\232", Q_NULLPTR));
        pushButton_3->setText(QApplication::translate("Lexical", "\344\277\235\345\255\230\346\226\207\344\273\266", Q_NULLPTR));
    } // retranslateUi

};

namespace Ui {
    class Lexical: public Ui_Lexical {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_LEXICAL_H
