/*
 * Copyright 2022 The JSpecify Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import clsx from 'clsx';
import styles from './styles.module.css';

const FeatureListSvg = [
  {
    title: 'Standard Annotations',
    Svg: require('@site/static/img/jspecify-landing-annot.svg').default,
    description: (
      <>
	JSpecify is releasing the first artifact of tool-independent annotations
        for powering static analysis checks in your Java code.
      </>
    ),
  },
  {
    title: 'Next Level Static Analysis',
    Svg: require('@site/static/img/jspecify-landing-bugs.svg').default,
    description: (
      <>
	JSpecify defines precise semantics, letting analysis tools find more
	bugs, and more consistently. Library owners won't have to decide which
        tool to support.
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
        JSpecify is developed by consensus of members representing a variety of
        stakeholders in Java static analysis, and we welcome your participation.
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
