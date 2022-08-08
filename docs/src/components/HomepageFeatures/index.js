import React from 'react';
import clsx from 'clsx';
import styles from './styles.module.css';

const FeatureListSvg = [
  {
    title: 'Standard Annotations',
    Svg: require('@site/static/img/jspecify-landing-annot.svg').default,
    description: (
      <>
        JSpecify defines a standard set of annotations with precise semantics
        covering the full range of Java&apos;s language features.
      </>
    ),
  },
  {
    title: 'Next Level Static Analysis',
    Svg: require('@site/static/img/jspecify-landing-bugs.svg').default,
    description: (
      <>
        JSpecify standard enables static analyzers to find more bugs.
        Furthermore, writing code against APIs with explicit nullness is just
        faster and more convenient.
      </>
    ),
  },
];

const FeatureListPng = [
  {
    title: 'Community Effort',
    Png: require('@site/static/img/jspecify-landing-community.png').default,
    description: (
      <>
        JSpecify is developed in collaboration ensuring that all voices are
        heard and the standard produced can be adopted by a wider Java community.
      </>
    ),
  },
];


function FeatureSvg({Svg, title, description}) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} role="img" />
      </div>
      <div className="text--center padding-horiz--md">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </div>
  );
}

function FeaturePng({Png, title, description}) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <img src={Png} className={styles.featureSvg} />
      </div>
      <div className="text--center padding-horiz--md">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures() {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureListSvg.map((props, idx) => (
            <FeatureSvg key={idx} {...props} />
          ))}
          {FeatureListPng.map((props, idx) => (
            <FeaturePng key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
