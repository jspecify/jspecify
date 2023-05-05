// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'JSpecify',
  tagline: 'Standard Annotations for Java Static Analysis',
  url: 'http://jspecify.org/',
  baseUrl: '/',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'throw',
  favicon: 'img/jspecify-favicon.ico',

  // GitHub pages deployment config.
  // If you aren't using GitHub pages, you don't need these.
  organizationName: 'jspecify', // Usually your GitHub org/user name.
  projectName: 'jspecify', // Usually your repo name.

  // Even if you don't use internalization, you can use this field to set useful
  // metadata like html lang. For example, if your site is Chinese, you may want
  // to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          sidebarPath: require.resolve('./sidebars.js'),
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          editUrl:
            'https://github.com/jspecify/jspecify/tree/main/docs/',
        },
        blog: {
          showReadingTime: true,
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          editUrl:
            'https://github.com/jspecify/jspecify/tree/main/docs/',
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      }),
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      navbar: {
        title: 'JSpecify',
        logo: {
          alt: 'JSpecify Logo',
          src: 'img/jspecify-logo.svg',
        },
        items: [
          {to: '/about', label: 'About Us', position: 'left'},
          {to: '/docs/start-here', label: 'Start Here', position: 'left'},
          {
            href: 'http://jspecify.org/docs/api/org/jspecify/annotations/package-summary.html',
            label: 'Javadoc',
            position: 'left',
          },
          {to: '/blog', label: 'Blog', position: 'left'},
          {
            href: 'https://github.com/jspecify/jspecify',
            label: 'GitHub',
            position: 'right',
          },
        ],
      },
      footer: {
        style: 'dark',
        links: [
          {
            title: 'Docs',
            items: [
              {
                label: 'Start Here',
                to: '/docs/start-here',
              },
              {
                label: 'Draft User Guide',
                to: '/docs/user-guide',
              },
              {
                label: 'Draft Javadoc',
                href: 'http://jspecify.org/docs/api/org/jspecify/annotations/package-summary.html',
              },
              {
                label: 'Draft Specification',
                to: '/docs/spec',
              },
            ],
          },
          {
            title: 'Contacts',
            items: [
              {
                label: 'Public Group',
                href: 'https://groups.google.com/g/jspecify-discuss'
              },
              {
                label: 'Mail The Team',
                href: 'mailto:jspecify-dev@googlegroups.com',
              },
            ],
          },
          {
            title: 'More',
            items: [
              {
                label: 'GitHub',
                href: 'https://github.com/jspecify/jspecify',
              },
              {
                label: 'Blog',
                to: '/blog',
              },
            ],
          },
        ],
        copyright: `Copyright ${new Date().getFullYear()} The JSpecify Authors. Built with Docusaurus.`,
      },
      prism: {
        theme: lightCodeTheme,
        darkTheme: darkCodeTheme,
        additionalLanguages: ['java'],
      },
    }),
};

module.exports = config;
