# Website

This website is built using [Docusaurus](https://docusaurus.io/), a modern static website generator.

The instructions bellow assume that you're inside `docs/` directory.

### Installation

```
$ npm install
```

### Local Development

```
$ npm start
```

This command starts a local development server and opens up a browser window. Most changes are reflected live without having to restart the server.

The development server has at least a couple downsides relative to a full build (an option that is discussed below):

- The development server is not as strict as the full build. If you see errors in CI that you don't see in local development, then try a full build locally.
- Directly opening a specific anchor in the development server (though a URL such as `http://localhost:3000/docs/nullness-design-faq/#unspecified-nullness`) does not work. (The problem seems to be that the server first returns a skeleton page (which naturally doesn't contain the anchor) and then fills in content later.) If you want to test navigation to anchors, it may work if you navigate by opening a link in the same tab (instead of in a new window or new tab or by typing the link into the address bar). Alternatively, you can build and serve the site locally.

### Build

```
$ npm run build
```

This command generates static content into the `build` directory and can be served using any static contents hosting service. One easy way to serve it is:

```
$ npm run serve
```

### Deployment

The site is built and published to GitHub Pages via GitHub Actions.

### Manual Deployment

Using SSH:

```
$ USE_SSH=true yarn deploy
```

Not using SSH:

```
$ GIT_USER=<Your GitHub username> yarn deploy
```

If you are using GitHub pages for hosting, this command is a convenient way to build the website and push to the `gh-pages` branch.
