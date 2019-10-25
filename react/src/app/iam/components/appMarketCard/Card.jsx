import React from 'react';
import './Card.scss';
import { Tooltip } from 'choerodon-ui/pro';
import { Badge, Icon } from 'choerodon-ui';
import { strLength } from '../../common/util';

const cssPrefix = 'app-market-card';


const Card = ({ category, name, img, contributor, description, hasNewVersion, free }) => {
  const descriptionContainer = (value) => {
    const strL = strLength(value);
    let width;
    if (strL > 340) {
      width = '3.4rem';
    } else {
      width = `${strL / 100}rem`;
    }
    return (
      <Tooltip title={<p style={{ width, whiteSpace: 'normal', marginBottom: 0 }}>{value}</p>}>
        <p className={`${cssPrefix}-mainBody-description`}>{description}</p>
      </Tooltip>
    );
  };
  return (
    <div className={`${cssPrefix}-container`} onTouchStart="this.classList.toggle('hover');">
      <div className={`${cssPrefix}-container-flipper`}>
        <div className={`${cssPrefix}-container-flipper-front`}>
          {free ? null : <div className="free-flag"><Icon className="money-icon" type="attach_money" /></div>}
          <img src={img} alt={name} className={`${cssPrefix}-mainBody-img`} />
          <h1 className={`${cssPrefix}-mainBody-title`}>
            <Badge dot={hasNewVersion} offset={[0, 6]}>
              <span>
                {name}
              </span>
            </Badge>
          </h1>
        </div>
        <div className={`${cssPrefix}-container-flipper-back`}>
          <h1 className={`${cssPrefix}-mainBody-title`}>
            <Tooltip title={name}>
              <Badge dot={hasNewVersion} offset={[0, 6]}>
                <span>
                  {name}
                </span>
              </Badge>
            </Tooltip>
          </h1>
          {descriptionContainer(description)}
        </div>
      </div>
      <div className={`${cssPrefix}-side`}>
        <p className={`${cssPrefix}-side-contributor`} title={contributor}>{contributor}</p>
        <p className={`${cssPrefix}-side-category`} title={category}>{category}</p>
      </div>
    </div>
  );
};

export default Card;
